package com.openmarket.db.actions;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import net.tomp2p.connection.Reservation;

import org.apache.commons.lang3.StringEscapeUtils;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.google.common.collect.ImmutableMap;
import com.openmarket.db.Tables.Answer;
import com.openmarket.db.Tables.AnswerVote;
import com.openmarket.db.Tables.Auction;
import com.openmarket.db.Tables.CategoryTreeView;
import com.openmarket.db.Tables.Login;
import com.openmarket.db.Tables.Product;
import com.openmarket.db.Tables.ProductBullet;
import com.openmarket.db.Tables.ProductPage;
import com.openmarket.db.Tables.ProductPicture;
import com.openmarket.db.Tables.ProductPrice;
import com.openmarket.db.Tables.Question;
import com.openmarket.db.Tables.QuestionVote;
import com.openmarket.db.Tables.Review;
import com.openmarket.db.Tables.ReviewVote;
import com.openmarket.db.Tables.Seller;
import com.openmarket.db.Tables.Shipping;
import com.openmarket.db.Tables.ShippingCost;
import com.openmarket.db.Tables.User;
import com.openmarket.tools.DataSources;
import com.openmarket.tools.TableConstants;
import com.openmarket.tools.Tools;

public class Actions {

	static final Logger log = LoggerFactory.getLogger(Actions.class);

	public static class UserActions {

		public static User createUserSimple(String email) {
			User user = User.create("email", email);
			Tools.writeRQL(user.toInsert());
			user = User.findFirst("email = ?", email);

			return user;
		}
		public static String userLogin(String email, String password, Response res) {

			User user = User.findFirst("email = ?", email);

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

			Login login = Login.findFirst("session_id = ? and expire_time > ?" , auth, now);

			if (login == null) {
				throw new NoSuchElementException("Please log in first");
			}

			return login;


		}

		public static User getUserFromAuth(String auth) {

			Login login = getLoginFromAuth(auth);

			User user = User.findById(login.getString("user_id"));



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

			String html = Tools.parseMustache(vars, DataSources.SIGNUP_EMAIL_TEMPLATE);

			String message = Tools.sendEmail(email, subject, html);

			return message;

		}

		public static User signupUserWithToken(String token, String password) {

			log.info("token = " + token);
			User user = User.findFirst("email_code = ?", token);

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

			Review r = Review.create("product_id", productId, "user_id", userId);
			Tools.writeRQL(r.toInsert());
			r = Review.findFirst(
					"product_id = ? and user_id = ?", productId, userId);
			return r;
		}

		public static String saveProductReview(String productId, String userId,
				String stars, String headline, String textHtml) {

			// See if the review first exists
			Review r = Review.findFirst(
					"product_id = ? and user_id = ?", productId, userId);

			String cmd;
			if (r != null) {
				cmd = Tools.toUpdate("review", r.getId().toString(),
						"stars", stars,
						"headline", headline,
						"text_html", textHtml);
			} else {
				cmd = Review.create("product_id", productId, 
						"user_id", userId,
						"stars", stars,
						"headline", headline,
						"text_html", textHtml).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product review saved";

			return message;


		}
		public static String deleteProductReview(String reviewId, String userId) {

			String message;
			Review r = Review.findFirst("id = ?", reviewId);

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
			ReviewVote r = ReviewVote.findFirst(
					"review_id = ? and user_id = ?", reviewId, userId);

			String cmd;
			if (r != null) {
				cmd = Tools.toUpdate("review_vote", r.getId().toString(),
						"vote", voteInt);
			} else {
				cmd = ReviewVote.create("review_id", reviewId, 
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
			QuestionVote r = QuestionVote.findFirst(
					"question_id = ? and user_id = ?", questionId, userId);

			String cmd;
			if (r != null) {
				cmd = Tools.toUpdate("question_vote", r.getId().toString(),
						"vote", voteInt);
			} else {
				cmd = QuestionVote.create("question_id", questionId, 
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
			AnswerVote r = AnswerVote.findFirst(
					"answer_id = ? and user_id = ?", answerId, userId);

			String cmd;
			if (r != null) {
				cmd = Tools.toUpdate("answer_vote", r.getId().toString(),
						"vote", voteInt);
			} else {
				cmd = AnswerVote.create("answer_id", answerId, 
						"user_id", userId,
						"vote", voteInt).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Answer vote saved";

			return message;
		}
		
		public static String askQuestion(String productId, String userId,
				String text) {
			
			Question q = Question.create("product_id", productId, "user_id", userId, "text", text);
			Tools.writeRQL(q.toInsert());
		
			String message = "Question saved";
			
			return message;
			
		}
		public static String answerQuestion(String questionId, String userId,
				String answer) {
			
			Answer a = Answer.create("question_id", questionId, "user_id", userId, "text", answer);
			Tools.writeRQL(a.toInsert());
		
			String message = "Answer saved";
			
			return message;
		}

	}


	public static class SellerActions {

		public static Seller createSellerSimple(String userId) {

			Seller seller = Seller.create("user_id", userId);
			Tools.writeRQL(seller.toInsert());
			seller = Seller.findFirst("user_id = ?", userId);

			return seller;
		}

		public static Seller getSeller(String userId) {
			Seller seller = Seller.findFirst("user_id = ?", userId);
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
			Product p = Product.create("seller_id", seller.getId().toString());

			Tools.writeRQL(p.toInsert());
			//			p = Product.findFirst("seller_id = ?, max(created_at)", seller.getId().toString());
			List<Product> ps = Product.where("seller_id = ?", seller.getId().toString())
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
			ProductPicture p = ProductPicture.findFirst(
					"product_id = ? and num_ = ?", productId, pictureNum);

			String cmd;
			if (p != null) {
				cmd = Tools.toUpdate("product_picture", p.getId().toString(),
						"product_id", productId, 
						"num_", pictureNum,
						"url", url);
			} else {
				cmd = ProductPicture.create("product_id", productId, 
						"num_", pictureNum,
						"url", url).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product picture #" + pictureNum + " saved";

			return message;
		}

		public static String saveProductDetails(String productId,
				String summerNoteHtml) {

			String escapedHTML = StringEscapeUtils.escapeHtml4(summerNoteHtml);
			// See if the ProductPage first exists
			ProductPage p = ProductPage.findFirst(
					"product_id = ?", productId);

			String cmd;
			if (p != null) {
				cmd = Tools.toUpdate("product_page", p.getId().toString(),
						"product_id", productId, 
						"product_html", escapedHTML);
			} else {
				cmd = ProductPage.create("product_id", productId, 
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
			ProductPrice p = ProductPrice.findFirst(
					"product_id = ?", productId);

			String cmd;
			if (p != null) {
				cmd = Tools.toUpdate("product_price", p.getId().toString(),
						"product_id", productId, 
						"price", price,
						"native_currency_id", currId,
						"variable_price", variablePrice,
						"price_select", priceSelect,
						"price_1", price1,
						"price_2", price2,
						"price_3", price3,
						"price_4", price4,
						"price_5", price5);
			} else {
				cmd = ProductPrice.create("product_id", productId, 
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
			Auction a = Auction.findFirst(
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
				cmd = Auction.create("product_id", productId, 
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

			Product p = Product.findFirst("id = ?", productId);

			if (!p.getString("seller_id").equals(seller.getId().toString())) {
				throw new NoSuchElementException("You don't own this product");
			}

		}

		public static String saveBullet(String productId, String bulletNum,
				String bullet) {
			// See if the picture first exists
			ProductBullet p = ProductBullet.findFirst(
					"product_id = ? and num_ = ?", productId, bulletNum);

			String cmd;
			if (p != null) {
				cmd = Tools.toUpdate("product_bullet", p.getId().toString(),
						"product_id", productId, 
						"num_", bulletNum,
						"text", bullet);
			} else {
				cmd = ProductBullet.create("product_id", productId, 
						"num_", bulletNum,
						"text", bullet).toInsert();
			}


			Tools.writeRQL(cmd);

			String message = "Product bullet #" + bulletNum + " saved";

			return message;
		}

		public static String deleteBullet(String productId, String bulletNum) {

			ProductBullet p = ProductBullet.findFirst(
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
			ProductPicture p = ProductPicture.findFirst(
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
			Shipping s = Shipping.findFirst("product_id = ?", productId);
			String cmd;
			if (s == null) {
				cmd = Shipping.create("product_id", productId).toInsert();
				Tools.writeRQL(cmd);
				s = Shipping.findFirst("product_id = ?", productId);
			}

			String shippingId = s.getId().toString();


			// See if the shipping cost first exists
			ShippingCost p = ShippingCost.findFirst(
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
				cmd = ShippingCost.create("shipping_id", shippingId, 
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

			Shipping s = Shipping.findFirst("product_id = ?", productId);
			String shippingId = s.getId().toString();

			ShippingCost p = ShippingCost.findFirst(
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
			Shipping a = Shipping.findFirst(
					"product_id = ?", productId);

			String cmd;
			if (a != null) {
				cmd = Tools.toUpdate("shipping", a.getId().toString(),
						"product_id", productId, 
						"from_country_id", fromCountryId);
			} else {
				cmd = Shipping.create("product_id", productId, 
						"from_country_id", fromCountryId).toInsert();
			}

			Tools.writeRQL(cmd);

			String message = "Shipping saved";

			return message;
		}
	}


	public static class CategoryActions {
		public static String getCategoryTree(String id) {


			if (id.equals("null")) {
				return CategoryTreeView.findFirst("id_1 = ?", 1).toJson(false, "id_1");
			} 

			CategoryTreeView c = CategoryTreeView.findFirst("id_1 = ? OR "
					+ "id_2 = ? OR "
					+ "id_3 = ? OR "
					+ "id_4 = ? OR "
					+ "id_5 = ? OR "
					+ "id_6 = ? OR " 
					+ "id_7 = ? ", id, id, id, id, id, id, id);

			return c.toJson(false);

		}
	}


	public static class WebActions {

		public static String setCookiesForLogin(User user, Response res) {

			Integer expireSeconds = TableConstants.EXPIRE_SECONDS;

			long now = new Date().getTime();

			long expireTime = now + expireSeconds*1000;


			String authenticatedSessionId = Tools.generateSecureRandom();

			// Not sure if this is necessary yet
			Boolean secure = false;

			// Store the users user in the DB, give them a session id
			Login login = Login.create("user_id", user.getId(),
					"session_id", authenticatedSessionId,
					"time_", now,
					"expire_time", expireTime);

			Tools.writeRQL(login.toInsert());


			// Set some cookies for that users login
			res.cookie("authenticated_session_id", authenticatedSessionId, expireSeconds, secure);
			res.cookie("uid", user.getId().toString(), expireSeconds, secure);
			//			String json = Tools.GSON2.toJson(cache);
			//			System.out.println(json);


			return "Logged in";


		}

		public static String getSessionId(Request req) {
			String sessionId = req.cookie("authenticated_session_id");
			return sessionId;
		}

	}
}
