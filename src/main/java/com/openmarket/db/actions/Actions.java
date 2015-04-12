package com.openmarket.db.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonNode;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.bitmerchant.db.Tables.Order;
import com.google.common.collect.ImmutableMap;
import com.openmarket.db.Tables.CategoryTreeView;
import com.openmarket.db.Tables.Seller;
import com.openmarket.db.Tables.User;
import com.openmarket.db.Tables.WishlistItem;
import com.openmarket.db.Transformations;
import com.openmarket.db.Tables.ProductThumbnailView;
import com.openmarket.tools.DataSources;
import com.openmarket.tools.TableConstants;
import com.openmarket.tools.Tools;

import static com.openmarket.db.Tables.*;

public class Actions {

	static final Logger log = LoggerFactory.getLogger(Actions.class);

	public static class UserActions {

		public static User createUserSimple(String email) {
			User user = USER.create("email", email);
			Tools.writeRQL(user.toInsert());
			user = USER.findFirst("email = ?", email);

			return user;
		}
		public static String userLogin(String email, String password, Response res) {

			User user = USER.findFirst("email = ?", email);

			if (user == null) {
				throw new NoSuchElementException("Incorrect email");
			}

			String encryptedPassword = user.getString("password_encrypted");

			Boolean correctPass = Tools.PASS_ENCRYPT.checkPassword(password, encryptedPassword);

			if (correctPass) {


				return WebActions.setCookiesForLogin(user, res);

			} else {
				throw new NoSuchElementException("Incorrect password");
			}


		}

		public static String userLogout(String auth) {


			Login login = getLoginFromAuth(auth);

			if (login == null) {
				throw new NoSuchElementException("User not logged in");
			}

			long now = new Date().getTime();

			// Set the expire time to right now
			String update = Tools.toUpdate("login", login.getId().toString(), 
					"expire_time", now);

			Tools.writeRQL(update);

			return "Logged out";
		}

		public static Login getLoginFromAuth(String auth) {

			long now = new Date().getTime();

			Login login = LOGIN.findFirst("session_id = ? and expire_time > ?" , auth, now);

			if (login == null) {
				throw new NoSuchElementException("Please log in first");
			}

			return login;


		}

		public static User getUserFromAuth(String auth) {

			Login login = getLoginFromAuth(auth);

			User user = USER.findById(login.getString("user_id"));



			return user;

		}

		public static User getUserFromSessionId(Request req) {
			String auth = WebActions.getSessionId(req);
			User user = getUserFromAuth(auth);

			return user;
		}

		public static String sendSignUpEmail(User user) {


			String email = user.getString("email");

			String subject = "OpenMarket Sign Up";

			String token = Tools.generateSecureRandom();

			// Store that token in the user row
			//			User userUpdate = user.set("email_code", token);



			String update = Tools.toUpdate("user", user.getId().toString(), "email_code", token);

			Tools.writeRQL(update);

			String url = DataSources.SET_PASSWORD_URL() + "?token=" + token;

			Map<String, Object> vars = ImmutableMap.<String, Object>builder()
					.put("url", url)
					.build();

			String html = Tools.parseMustache(vars, DataSources.SIGNUP_EMAIL_TEMPLATE());

			String message = Tools.sendEmail(email, null, subject, html);

			return message;

		}

		public static User signupUserWithToken(String token, String password) {

			log.info("token = " + token);
			User user = USER.findFirst("email_code = ?", token);

			if (user != null) {
				// Encrypt the password
				String encryptedPass = Tools.PASS_ENCRYPT.encryptPassword(password);

				String update = Tools.toUpdate("user", user.getId().toString(), 
						"password_encrypted", encryptedPass,
						"authenticated", true,
						"email_code", null);

				Tools.writeRQL(update);

				//				String message = "Password set";

				// Now log the user in
				return user;

			} else {
				throw new NoSuchElementException("Incorrect email code");
			}


		}

		public static Review createProductReview(String productId, String userId) {

			Review r = REVIEW.create("product_id", productId, "user_id", userId);
			Tools.writeRQL(r.toInsert());
			r = REVIEW.findFirst(
					"product_id = ? and user_id = ?", productId, userId);
			return r;
		}

		public static String saveProductReview(String productId, String userId,
				String stars, String headline, String textHtml) {

			String escapedHTML = StringEscapeUtils.escapeHtml4(textHtml).replace(";","semicolon");
			// See if the review first exists
			Review r = REVIEW.findFirst(
					"product_id = ? and user_id = ?", productId, userId);

			String cmd;
			if (r != null) {
				cmd = Tools.toUpdate("review", r.getId().toString(),
						"stars", stars,
						"headline", headline,
						"text_html", escapedHTML);
			} else {
				cmd = REVIEW.create("product_id", productId, 
						"user_id", userId,
						"stars", stars,
						"headline", headline,
						"text_html", escapedHTML).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product review saved";

			return message;


		}
		public static String deleteProductReview(String reviewId, String userId) {

			String message;
			Review r = REVIEW.findFirst("id = ?", reviewId);

			if (r != null) {

				String cmd = Tools.toDelete("review", r.getId().toString());
				Tools.writeRQL(cmd);

				message = "review deleted";
			} else {
				message = "BLARP";
			}

			return message;

		}
		public static String voteOnReview(String reviewId, String userId,
				String vote) {

			Integer voteInt = (vote.equals("up")) ? 1 : 0;
			// See if the review first exists
			ReviewVote r = REVIEW_VOTE.findFirst(
					"review_id = ? and user_id = ?", reviewId, userId);

			log.info("review id = " + reviewId);
			String cmd;
			if (r != null) {
				r.set("vote", vote);
				cmd = r.toUpdate();
			} else {
				cmd = REVIEW_VOTE.create("review_id", reviewId, 
						"user_id", userId,
						"vote", voteInt).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Review vote saved";

			return message;
		}

		public static String voteOnQuestion(String questionId, String userId,
				String vote) {

			Integer voteInt = (vote.equals("up")) ? 1 : -1;
			// See if the review first exists
			QuestionVote r = QUESTION_VOTE.findFirst(
					"question_id = ? and user_id = ?", questionId, userId);

			String cmd;
			if (r != null) {
				cmd = Tools.toUpdate("question_vote", r.getId().toString(),
						"vote", voteInt);
			} else {
				cmd = QUESTION_VOTE.create("question_id", questionId, 
						"user_id", userId,
						"vote", voteInt).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Question vote saved";

			return message;
		}

		public static String voteOnAnswer(String answerId, String userId,
				String vote) {

			Integer voteInt = (vote.equals("up")) ? 1 : -1;
			// See if the review first exists
			AnswerVote r = ANSWER_VOTE.findFirst(
					"answer_id = ? and user_id = ?", answerId, userId);

			String cmd;
			if (r != null) {
				cmd = Tools.toUpdate("answer_vote", r.getId().toString(),
						"vote", voteInt);
			} else {
				cmd = ANSWER_VOTE.create("answer_id", answerId, 
						"user_id", userId,
						"vote", voteInt).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Answer vote saved";

			return message;
		}

		public static String askQuestion(String productId, String userId,
				String text) {

			Question q = QUESTION.create("product_id", productId, "user_id", userId, "text", text);
			Tools.writeRQL(q.toInsert());

			String message = "Question saved";

			return message;

		}
		public static String answerQuestion(String questionId, String userId,
				String answer) {

			Answer a = ANSWER.create("question_id", questionId, "user_id", userId, "text", answer);
			Tools.writeRQL(a.toInsert());

			String message = "Answer saved";

			return message;
		}

		public static String addToCart(String userId, String productId) {


			// Check if item already exists
			CartItem cartItem = CART_ITEM.findFirst("user_id = ? and product_id = ? and purchased = ?", userId,
					productId, "0");
			if (cartItem != null) {
				Integer quantity = cartItem.getInteger("quantity");
				cartItem.set("quantity", ++quantity);
				log.info("update = " + cartItem.toUpdate());
				Tools.writeRQL(cartItem.toUpdate());

			} else {
				// Add the product to the cart
				cartItem = CART_ITEM.create("user_id", userId,
						"product_id", productId,
						"quantity", 1);
				Tools.writeRQL(cartItem.toInsert());
			}

			String message = "Item added to cart";

			return message;
		}

		public static String removeFromCart(String userId, String cartItemId) {

			CartItem ci = CART_ITEM.findFirst("id = ?", cartItemId);
			String message;
			if (ci != null) {

				String cmd = Tools.toDelete("cart_item", ci.getId().toString());
				Tools.writeRQL(cmd);

				message = "Item removed from cart";
			} else {
				throw new NoSuchElementException("Couldn't delete the cart item");
			}

			return message;

		}

		public static String saveAddress(String userId, String fullName,
				String street, String addrTwo, String city, String state,
				String zipcode, String countryId) {

			Address address = ADDRESS.create("user_id", userId,
					"full_name", fullName,
					"address_line_1", street,
					"address_line_2", addrTwo,
					"city", city,
					"state", state,
					"zip", zipcode,
					"default_", "0", //TODO default
					"country_id", countryId); 

			Tools.writeRQL(address.toInsert());

			String message = "Address saved";

			return message;

		}

		public static String editAddress(String userId, String addressId, String fullName,
				String street, String addrTwo, String city, String state,
				String zipcode, String countryId) {

			Address address = ADDRESS.findFirst("id = ?", addressId);

			address.set("user_id", userId,
					"full_name", fullName,
					"address_line_1", street,
					"address_line_2", addrTwo,
					"city", city,
					"state", state,
					"zip", zipcode,
					"default_", "0", //TODO default
					"country_id", countryId); 

			Tools.writeRQL(address.toUpdate());

			String message = "Address changed";

			return message;

		}


		public static String deleteAddress(String userId, String addressId) {

			String message;
			Address addr = ADDRESS.findFirst("id = ?", addressId);

			if (addr != null) {

				String cmd = Tools.toDelete("address", addr.getId().toString());
				Tools.writeRQL(cmd);

				message = "Address deleted";
			} else {
				message = "BLARP";
			}

			return message;


		}
		public static String saveShipment(String userId, String addressId,
				String sellerId) {
			String message;
			// First, find all the active cart items for that user
			List<CartView> cvs = CART_VIEW.find("user_id = ? and seller_id = ?", 
					userId, sellerId);

			Set<String> cartItemIds = new HashSet<String>();

			// These need to be updated 
			String shipmentId = null;
			for (CartView cv : cvs) {
				cartItemIds.add(cv.getId().toString());
				String cartShipment = cv.getString("shipment_id");
				log.info(cartShipment);
				if (cartShipment != null) {
					shipmentId = cartShipment;
				}
			}

			// If the shipment ID isn't null, then it needs to be updated, and don't change the cart_items
			if (shipmentId != null) {
				Shipment shipment = SHIPMENT.findFirst("id = ?", shipmentId);
				shipment.set("address_id",addressId);

				Tools.writeRQL(shipment.toUpdate());
				message = "Shipping address updated";
			} 
			// Otherwise, add a new shipment, update the cart items
			else {
				Shipment shipment = SHIPMENT.create("address_id",addressId);

				Tools.writeRQL(shipment.toInsert());


			}

			// Always update the cart items

			LazyList<Shipment> shipments = SHIPMENT.find("address_id = ? and tracking_url is NULL"
					,addressId).orderBy("created_at desc");
			Shipment shipment = shipments.get(0);

			String cmd = Tools.toUpdate("cart_item", cartItemIds, 
					"shipment_id", shipment.getId().toString());
			log.info("The shipment id = " + shipment.getId().toString() + " and the update line is \n" + cmd);

			Tools.writeRQL(cmd);
			message = "Shipping address added";


			return message;


		}

		public static void updatePaymentIframe(String paymentId, CartGroup cg) {

			String total = cg.getString("checkout_total");
			String iso = cg.getString("iso");

			String bitmerchantAddress = cg.getString("bitmerchant_address");

			String jsonReq = Tools.createBitmerchantButtonRequest(total, iso, paymentId);

			// Create the bitmerchant order and iframe stuff
			//			Order o = com.bitmerchant.db.Actions.OrderActions.createOrder(jsonReq);

			JsonNode jn = Tools.sendBitmerchantRequestToSeller(jsonReq, bitmerchantAddress);
			
			log.info(jn.toString());

			String buttonId = jn.get("order").get("button_id").asText();
			//			String buttonId = o.getString("button_id");

			Payment payment = PAYMENT.findFirst("id = ?", paymentId);

			String iframeText = Tools.createBitmerchantIframe(buttonId,
					bitmerchantAddress);


			log.info(iframeText);
			payment.set("order_iframe",iframeText);

			log.info("payment iframe update = " + payment.toUpdate());
			Tools.writeRQL(payment.toUpdate());

		}

		public static String createPayment(String userId, String sellerId) {

			String message;

			// Find all the active cart items for that user
			List<CartView> cvs = CART_VIEW.find("user_id = ? and seller_id = ?", 
					userId, sellerId);

			if (cvs.size() == 0) {
				throw new NoSuchElementException("Nothing in cart");
			}

			// Construct the order jsonReq from cart group totals
			CartGroup cg = CART_GROUP.findFirst("user_id = ? and seller_id = ?",
					userId, sellerId);



			Set<String> cartItemIds = new HashSet<String>();

			// These need to be updated 
			String paymentId = null;
			for (CartView cv : cvs) {
				cartItemIds.add(cv.getId().toString());
				String cartPayment = cv.getString("payment_id");
				log.info(cartPayment);
				if (cartPayment != null) {
					paymentId = cartPayment;
				}
			}


			// If the payment ID isn't null, then it needs to be updated, and don't change the cart_items
			if (paymentId != null) {
				//				Payment payment = Payment.findFirst("id = ?", paymentId);
				message = "payment updated";
			}
			// Otherwise, add a payment, and update the cart items
			else {
				Payment payment = PAYMENT.create();

				Tools.writeRQL(payment.toInsert());


			}
			// Always update the cart items
			// Fetch it to get its id
			LazyList<Payment> payments = PAYMENT.find("completed = 0").orderBy("created_at desc");
			Payment payment = payments.get(0);
			paymentId = payment.getId().toString();

			String cmd = Tools.toUpdate("cart_item", cartItemIds, 
					"payment_id", payment.getId().toString());
			log.info("The payment id = " + payment.getId().toString() + " and the update line is \n" + cmd);

			Tools.writeRQL(cmd);
			message = "Payment added";

			updatePaymentIframe(paymentId, cg);



			return message;

		}
		public static String getWishlistThumbnails(String userId) {

			// Get users wishlist items
			List<WishlistItem> wsItems = WISHLIST_ITEM.find("user_id = ?", userId);

			if (wsItems.size() == 0) {
				return "{\"products\": []}";
			}
			// Construct an in clause
			StringBuilder inClause = new StringBuilder();
			inClause.append("(");
			Iterator<WishlistItem> wsI = wsItems.iterator();
			for (;;) {
				WishlistItem ws = wsI.next();
				inClause.append(ws.getString("product_id"));
				if (wsI.hasNext()) {
					inClause.append(",");
				} else {
					inClause.append(")");
					break;
				}
			}
			log.info("in clause = " + inClause);

			List<ProductThumbnailView> pvs = PRODUCT_THUMBNAIL_VIEW.where(
					"product_id in " + inClause.toString());

			String json = Tools.nodeToJson(
					Transformations.productThumbnailViewJson(pvs));

			return json;
		}

		public static String addToWishlist(String userId, String productId) {


			// Check if item already exists
			WishlistItem wishlistItem = WISHLIST_ITEM.findFirst("user_id = ? and product_id = ? and purchased = ?", userId,
					productId, "0");

			// only add it if its not already there
			if (wishlistItem == null){
				// Add the product to the cart
				wishlistItem = WISHLIST_ITEM.create("user_id", userId,
						"product_id", productId,
						"purchased", 0);
				Tools.writeRQL(wishlistItem.toInsert());
			}

			String message = "Item added to wishlist";

			return message;
		}
		public static String removeFromWishlist(String userId, String productId) {

			WishlistItem ci = WISHLIST_ITEM.findFirst("user_id = ? and product_id = ?",
					userId, productId);
			String message;
			if (ci != null) {

				String cmd = Tools.toDelete("wishlist_item", ci.getId().toString());
				Tools.writeRQL(cmd);

				message = "Item removed from wishlist";
			} else {
				throw new NoSuchElementException("Couldn't remove the wishlist item");
			}

			return message;
		}
		public static String saveUsername(User user, String username) {

			user.set("name",username);
			String cmd = user.toUpdate();
			Tools.writeRQL(cmd);

			return "User name saved";
		}
		public static void createFeedbackFromPaymentSuccess(String paymentId) {

			// find all the cart_item ids that match that paymentId
			List<CartItem> cis = CART_ITEM.find("payment_id = ?", paymentId);

			// insert empty feedback rows
			for (CartItem ci : cis) {
				String cmd = FEEDBACK.create("cart_item_id", ci.getId().toString()).toInsert();
				Tools.writeRQL(cmd);
			}

			log.info("feedback rows created for payment id = " + paymentId);
		}
		public static String saveFeedback(String feedbackId, String userId,
				String stars, String arrivedOnTime, String correctlyDescribed,
				String promptService, String comments) {


			String escapedHTML = (comments != null) ?
					StringEscapeUtils.escapeHtml4(comments).replace(";","semicolon") : null;
					// See if the review first exists

					Feedback r = FEEDBACK.findFirst("id = ?", feedbackId);

					if (r != null) {
						r.set("stars", stars,
								"arrived_on_time", arrivedOnTime,
								"correctly_described", correctlyDescribed,
								"prompt_service", promptService,
								"comments", escapedHTML);
					} 


					Tools.writeRQL(r.toUpdate());

					String message = "Feedback saved";

					return message;

		}
		public static String sendNote(User user, 
				String shipmentId, String messageHtml) {

			String note = StringEscapeUtils.unescapeHtml4(messageHtml);

			OrderView ov = ORDER_VIEW.findFirst("shipment_id = ?", shipmentId);

			// Get the seller info 
			log.info(ov.toJson(true));
			String sellerId = ov.getString("seller_id");
			Seller seller = SELLER.findFirst("id = ?", sellerId);
			String sellerUserId = seller.getString("user_id");
			String productName = ov.getString("title");



			String subject = "OpenMarket Message from Customer";

			Map<String, Object> vars = ImmutableMap.<String, Object>builder()
					.put("user_name", user.getString("name"))
					.put("product_name", productName)
					.put("message", note)
					.build();

			String html = Tools.parseMustache(vars, DataSources.TO_SELLER_MESSAGE_TEMPLATE());

			String userEmail = user.getString("email");

			String sellerEmail = USER.findFirst("id = ?", sellerUserId).getString("email");

			String message = Tools.sendEmail(sellerEmail, userEmail, subject, html);

			return message;

		}


	}


	public static class PaymentActions {
		public static void updatePayment(String paymentId) {


			// update the payment to completed
			Payment payment = PAYMENT.findFirst("id = ?", paymentId);
			payment.set("completed","1");

			Tools.writeRQL(payment.toUpdate());

			// update the cart_items to purchased
			String cmd = Tools.toUpdate("cart_item","payment_id",Integer.valueOf(paymentId),"purchased","1");
			Tools.writeRQL(cmd);
			log.info(cmd);


		}
	}


	public static class SellerActions {

		public static Seller createSellerSimple(String userId) {

			Seller seller = SELLER.create("user_id", userId);
			Tools.writeRQL(seller.toInsert());
			seller = SELLER.findFirst("user_id = ?", userId);

			return seller;
		}

		public static Seller getSeller(String userId) {
			Seller seller = SELLER.findFirst("user_id = ?", userId);
			return seller;
		}



		public static String saveShopName(Seller seller, String shopName) {
			String cmd = Tools.toUpdate("seller", seller.getId().toString(), 
					"shop_name", shopName);
			Tools.writeRQL(cmd);

			return "Shop name saved";
		}

		public static Seller getSellerFromSessionId(Request req) {
			User user = UserActions.getUserFromSessionId(req);
			Seller seller = SellerActions.getSeller(user.getId().toString());

			if (seller == null) {
				throw new NoSuchElementException("Seller not logged in");
			}

			return seller;
		}

		public static Product createNewProduct(Seller seller) {
			Product p = PRODUCT.create("seller_id", seller.getId().toString());

			Tools.writeRQL(p.toInsert());
			//			p = Product.findFirst("seller_id = ?, max(created_at)", seller.getId().toString());
			List<Product> ps = PRODUCT.where("seller_id = ?", seller.getId().toString())
					.orderBy("created_at desc");
			return ps.get(0);

		}

		public static String setProductInfo(String productId, String title, 
				String quantity, String processingTime) {

			String cmd = Tools.toUpdate("product", productId, 
					"title", title,
					"quantity", quantity,
					"processing_time_span_id", processingTime);

			Tools.writeRQL(cmd);

			String message = "Product info set";

			return message;

		}

		public static String savePicture(String productId, String pictureNum,
				String url) {

			// See if the picture first exists
			ProductPicture p = PRODUCT_PICTURE.findFirst(
					"product_id = ? and num_ = ?", productId, pictureNum);

			String cmd;
			if (p != null) {
				cmd = Tools.toUpdate("product_picture", p.getId().toString(),
						"product_id", productId, 
						"num_", pictureNum,
						"url", url);
			} else {
				cmd = PRODUCT_PICTURE.create("product_id", productId, 
						"num_", pictureNum,
						"url", url).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product picture #" + pictureNum + " saved";

			return message;
		}

		public static String saveProductDetails(String productId,
				String summerNoteHtml) {

			String escapedHTML = StringEscapeUtils.escapeHtml4(summerNoteHtml).replace(";","semicolon");
			log.info("escaped html = " + escapedHTML);
			// See if the ProductPage first exists
			ProductPage p = PRODUCT_PAGE.findFirst(
					"product_id = ?", productId);

			String cmd;
			if (p != null) {
				cmd = Tools.toUpdate("product_page", p.getId().toString(),
						"product_id", productId, 
						"product_html", escapedHTML);
			} else {
				cmd = PRODUCT_PAGE.create("product_id", productId, 
						"product_html", escapedHTML).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product page saved";

			return message;
		}

		public static String saveProductPrice(String productId,
				String price, String currId, String variablePrice,
				String priceSelect, String price1, String price2,
				String price3, String price4, String price5) {

			// See if the ProductPrice first exists
			ProductPrice p = PRODUCT_PRICE.findFirst(
					"product_id = ?", productId);

			String cmd;
			if (p != null) {
				p.set("product_id", productId, 
						"price", price,
						"native_currency_id", currId,
						"variable_price", variablePrice,
						"price_select", priceSelect,
						"price_1", price1,
						"price_2", price2,
						"price_3", price3,
						"price_4", price4,
						"price_5", price5);

				cmd = p.toUpdate();

			} else {
				cmd = PRODUCT_PRICE.create("product_id", productId, 
						"price", price,
						"native_currency_id", currId,
						"variable_price", variablePrice,
						"price_select", priceSelect,
						"price_1", price1,
						"price_2", price2,
						"price_3", price3,
						"price_4", price4,
						"price_5", price5).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product price saved";

			return message;

		}

		public static String saveAuction(String productId, String isAuction,
				String auctionExpirationDate, String auctionStartPrice,
				String auctionReservePrice, String currIso) {

			// First update the product to say that its an auction
			String updateProduct = Tools.toUpdate("product", productId, 
					"auction", isAuction);

			Tools.writeRQL(updateProduct);

			// See if the ProductPrice first exists
			Auction a = AUCTION.findFirst(
					"product_id = ?", productId);

			String cmd;
			if (a != null) {
				cmd = Tools.toUpdate("auction", a.getId().toString(),
						"product_id", productId, 
						"expire_time", auctionExpirationDate,
						"start_amount", auctionStartPrice,
						"reserve_amount", auctionReservePrice,
						"currency_id", currIso
						);
			} else {
				cmd = AUCTION.create("product_id", productId, 
						"expire_time", auctionExpirationDate,
						"start_amount", auctionStartPrice,
						"reserve_amount", auctionReservePrice,
						"currency_id", currIso).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Auction saved";

			return message;

		}

		public static String saveProductCategory(String productId,
				String productCategory) {

			String cmd = Tools.toUpdate("product", productId, 
					"category_id", productCategory);

			Tools.writeRQL(cmd);

			String message = "Product category set";

			return message;
		}

		public static void ensureSellerOwnsProduct(Request req, String productId) {
			Seller seller = getSellerFromSessionId(req);

			Product p = PRODUCT.findFirst("id = ?", productId);

			if (!p.getString("seller_id").equals(seller.getId().toString())) {
				throw new NoSuchElementException("You don't own this product");
			}

		}

		public static String saveBullet(String productId, String bulletNum,
				String bullet) {
			// See if the picture first exists
			ProductBullet p = PRODUCT_BULLET.findFirst(
					"product_id = ? and num_ = ?", productId, bulletNum);

			String cmd;
			if (p != null) {
				cmd = Tools.toUpdate("product_bullet", p.getId().toString(),
						"product_id", productId, 
						"num_", bulletNum,
						"text", bullet);
			} else {
				cmd = PRODUCT_BULLET.create("product_id", productId, 
						"num_", bulletNum,
						"text", bullet).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product bullet #" + bulletNum + " saved";

			return message;
		}

		public static String deleteBullet(String productId, String bulletNum) {

			ProductBullet p = PRODUCT_BULLET.findFirst(
					"product_id = ? and num_ = ?", productId, bulletNum);

			String message;
			String cmd;
			if (p != null) {
				cmd = Tools.toDelete("product_bullet", p.getId().toString());

				Tools.writeRQL(cmd);

				message = "Product bullet #" + bulletNum + " deleted";
			} else {
				message = "Couldn't find the bullet";
			}

			return message;

		}

		public static String deletePicture(String productId, String pictureNum) {
			ProductPicture p = PRODUCT_PICTURE.findFirst(
					"product_id = ? and num_ = ?", productId, pictureNum);

			String message;
			String cmd;
			if (p != null) {
				cmd = Tools.toDelete("product_picture", p.getId().toString());

				Tools.writeRQL(cmd);

				message = "Product picture #" + pictureNum + " deleted";
			} else {
				message = "Couldn't find the picture";
			}

			return message;
		}

		public static String saveShippingCost(String productId,
				String shippingNum, String toCountryId, String price,
				String nativeCurrId) {

			// First, create or fetch the shipping row(its not the product row)
			Shipping s = SHIPPING.findFirst("product_id = ?", productId);
			String cmd;
			if (s == null) {
				cmd = SHIPPING.create("product_id", productId).toInsert();
				Tools.writeRQL(cmd);
				s = SHIPPING.findFirst("product_id = ?", productId);
			}

			String shippingId = s.getId().toString();


			// See if the shipping cost first exists
			ShippingCost p = SHIPPING_COST.findFirst(
					"shipping_id = ? and num_ = ?", shippingId, shippingNum);

			cmd = new String();
			if (p != null) {
				cmd = Tools.toUpdate("shipping_cost", p.getId().toString(),
						"shipping_id", shippingId, 
						"num_", shippingNum,
						"to_country_id", toCountryId,
						"price", price,
						"native_currency_id", nativeCurrId);
			} else {
				cmd = SHIPPING_COST.create("shipping_id", shippingId, 
						"num_", shippingNum,
						"to_country_id", toCountryId,
						"price", price,
						"native_currency_id", nativeCurrId).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product Shipping cost  #" + shippingNum + " saved";

			return message;
		}	

		public static String deleteShippingCost(String productId, String shippingNum) {

			Shipping s = SHIPPING.findFirst("product_id = ?", productId);
			String shippingId = s.getId().toString();

			ShippingCost p = SHIPPING_COST.findFirst(
					"shipping_id = ? and num_ = ?", shippingId, shippingNum);

			String message;
			String cmd;
			if (p != null) {
				cmd = Tools.toDelete("shipping_cost", p.getId().toString());

				Tools.writeRQL(cmd);

				message = "Product shipping #" + shippingNum + " deleted";
			} else {
				message = "Couldn't find that shipping info";
			}

			return message;
		}

		public static String saveShipping(String productId, String fromCountryId) {

			// See if the shipping first exists
			Shipping a = SHIPPING.findFirst(
					"product_id = ?", productId);

			String cmd;
			if (a != null) {
				cmd = Tools.toUpdate("shipping", a.getId().toString(),
						"product_id", productId, 
						"from_country_id", fromCountryId);
			} else {
				cmd = SHIPPING.create("product_id", productId, 
						"from_country_id", fromCountryId).toInsert();
			}

			Tools.writeRQL(cmd);

			String message = "Shipping saved";

			return message;
		}

		public static String saveShippingPhysical(String productId,
				String isPhysical) {

			// See if the shipping first exists
			Product a = PRODUCT.findFirst(
					"id = ?", productId);

			String cmd;

			cmd = Tools.toUpdate("product", a.getId().toString(),
					"id", productId, 
					"physical", isPhysical);

			Tools.writeRQL(cmd);

			String message = "Is physical saved";

			return message;
		}

		public static String saveTrackingUrl(String shipmentId, Seller seller,
				String trackingUrl) {



			Shipment shipment = SHIPMENT.findFirst("id = ?", shipmentId);

			shipment.set("tracking_url", trackingUrl);


			Tools.writeRQL(shipment.toUpdate());

			// Get the user for the order:
			String userId = ORDER_GROUP.findFirst("shipment_id = ?", shipmentId).getString("user_id");

			User user = USER.findFirst("id = ?", userId);

			// Get the seller user for the email
			User sellerUser = USER.findFirst("id = ?", seller.getString("user_id"));

			String toEmail = user.getString("email");
			String replyToEmail = sellerUser.getString("email");

			sendUpdatedTrackingEmail(trackingUrl, toEmail, replyToEmail);

			String message = "Tracking URL saved, and an email sent to user notifying them.";

			return message;
		}

		public static String sendUpdatedTrackingEmail(String trackingUrl, 
				String userEmail, String sellerEmail) {

			String subject = "OpenMarket Order Shipping Information";

			Map<String, Object> vars = ImmutableMap.<String, Object>builder()
					.put("tracking_url", trackingUrl)
					.build();

			String html = Tools.parseMustache(vars, DataSources.UPDATE_SHIPPING_TEMPLATE());


			String message = Tools.sendEmail(userEmail, sellerEmail, subject, html);

			return message;

		}
	}


	public static class CategoryActions {
		public static String getCategoryTree(String id) {


			if (id.equals("null")) {
				return CATEGORY_TREE_VIEW.findFirst("id_1 = ?", 1).toJson(false, "id_1");
			} 

			CategoryTreeView c = CATEGORY_TREE_VIEW.findFirst("id_1 = ? OR "
					+ "id_2 = ? OR "
					+ "id_3 = ? OR "
					+ "id_4 = ? OR "
					+ "id_5 = ? OR "
					+ "id_6 = ? OR " 
					+ "id_7 = ? ", id, id, id, id, id, id, id);

			return c.toJson(false);

		}

		public static Set<Integer> getAllCategoryChildren(Integer categoryId) {
			String query = "id_1 = " + categoryId + " or "
					+ "id_2 = " + categoryId + " or "
					+ "id_3 = " + categoryId + " or "
					+ "id_4 = " + categoryId + " or "
					+ "id_5 = " + categoryId + " or "
					+ "id_6 = " + categoryId + " or "
					+ "id_7 = " + categoryId + "";
			List<CategoryTreeView> cvs = CATEGORY_TREE_VIEW.find(query);

			Set<Integer> categoryIds = new LinkedHashSet<Integer>();

			for (CategoryTreeView cv : cvs) {
				// loop through the 7
				for (int i = 1; i < 8; i++) {
					Integer id = cv.getInteger("id_" + i);
					if (id != null && id > categoryId) {
						categoryIds.add(id);
					}
				}

			}

			return categoryIds;
		}

		public static String getCategoryThumbnails(String categoryId) {
			// have to get all the children here
			Set<Integer> children = getAllCategoryChildren(Integer.parseInt(categoryId));
			children.add(Integer.parseInt(categoryId));

			// Construct an in clause
			StringBuilder inClause = new StringBuilder();
			inClause.append("(");
			Iterator<Integer> childrenIt = children.iterator();
			for (;;) {
				Integer id = childrenIt.next();
				inClause.append(id);
				if (childrenIt.hasNext()) {
					inClause.append(",");
				} else {
					inClause.append(")");
					break;
				}
			}
			log.info("in clause = " + inClause);

			List<ProductThumbnailView> pvs = PRODUCT_THUMBNAIL_VIEW.where(
					"category_id in " + inClause.toString());

			String json = Tools.nodeToJson(Transformations.productThumbnailViewJson(pvs));

			return json;
		}




	}


	public static class WebActions {

		public static String setCookiesForLogin(User user, Response res) {

			Integer expireSeconds = TableConstants.EXPIRE_SECONDS;

			long now = new Date().getTime();

			long expireTime = now + expireSeconds*1000;


			String authenticatedSessionId = Tools.generateSecureRandom();

			// Not sure if this is necessary yet
			Boolean secure = DataSources.IS_SSL;

			// Store the users user in the DB, give them a session id
			Login login = LOGIN.create("user_id", user.getId(),
					"session_id", authenticatedSessionId,
					"time_", now,
					"expire_time", expireTime);

			Tools.writeRQL(login.toInsert());


			// Set some cookies for that users login
			res.cookie("authenticated_session_id", authenticatedSessionId, expireSeconds, secure);
			res.cookie("uid", user.getId().toString(), expireSeconds, secure);
			//			String json = Tools.GSON2.toJson(cache);
			//			System.out.println(json);


			// see if the user is actually a seller, if it is, change the message to a seller one,
			// in order to redirect the page, and set the bitmerchant address
			Seller seller = SellerActions.getSeller(user.getId().toString());
			if (seller != null) {
				seller.set("bitmerchant_address", DataSources.WEB_SERVICE_EXTERNAL_URL());
				Tools.writeRQL(seller.toUpdate());
				return "Logged in as a seller";
			} else {
				return "Logged in";
			}


		}

		public static String getSessionId(Request req) {
			String sessionId = req.cookie("authenticated_session_id");
			return sessionId;
		}


	}
}
