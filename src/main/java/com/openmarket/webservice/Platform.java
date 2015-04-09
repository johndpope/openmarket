package com.openmarket.webservice;

import static spark.Spark.get;
import static spark.Spark.post;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.server.UserIdentity;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitmerchant.db.Tables.Order;
import com.bitmerchant.db.Tables.OrderView;
import com.openmarket.db.Transformations;
import com.openmarket.db.actions.Actions.CategoryActions;
import com.openmarket.db.actions.Actions.PaymentActions;
import com.openmarket.db.actions.Actions.SellerActions;
import com.openmarket.db.actions.Actions.UserActions;
import com.openmarket.db.actions.Actions.WebActions;
import com.openmarket.tools.DataSources;
import com.openmarket.tools.Tools;

import static com.openmarket.db.Tables.*;

public class Platform {

	static final Logger log = LoggerFactory.getLogger(Platform.class);


	public static void setup() {




		get("/home/:derp", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return "ur a NEWB";
		});

		post("/send_signup_email", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				String email = Tools.createMapFromAjaxPost(req.body()).get("email");

				Tools.dbInit();

				User user = UserActions.createUserSimple(email);

				// If its a local IP, also create the seller too
				Seller seller = SellerActions.createSellerSimple(user.getId().toString());

				String message = UserActions.sendSignUpEmail(user);


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/send_reset_password_email", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);
				
				String message = UserActions.sendSignUpEmail(user);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_password", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				String password = vars.get("password");
				String token = vars.get("token");

				Tools.dbInit();
				User user = UserActions.signupUserWithToken(token, password);
				String email = user.getString("email");



				String message = UserActions.userLogin(email, password, res);

				// see if the user is actually a seller, if it is, change the message to a seller one,
				// in order to redirect the page
				Seller seller = SellerActions.getSeller(user.getId().toString());
				if (seller != null) {
					message = "Logged in as a seller";
				}




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/login", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				String password = vars.get("password");
				String email = vars.get("email");

				Tools.dbInit();

				String message = UserActions.userLogin(email, password, res);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/logout", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String auth = req.cookie("authenticated_session_id");

				// remove the key, and save the map
				Tools.dbInit();
				String message = UserActions.userLogout(auth);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/save_shop_name", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				String shopName = vars.get("shop_name");

				Tools.dbInit();
				com.bitmerchant.tools.Tools.dbInit();
				Seller seller = SellerActions.getSellerFromSessionId(req);

				com.bitmerchant.db.Actions.saveMerchantInfo(shopName, "USD");
				String message = SellerActions.saveShopName(seller, shopName);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				com.bitmerchant.tools.Tools.dbClose();
				Tools.dbClose();
			}

		});
		
		post("/save_username", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				String username = vars.get("username");

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);

				String message = UserActions.saveUsername(user, username);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});


		post("/create_product", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Tools.dbInit();

				Seller seller = SellerActions.getSellerFromSessionId(req);

				Product p = SellerActions.createNewProduct(seller);

				String message = p.getId().toString();


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_info/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				String title = vars.get("title");
				String quantity = vars.get("quantity");
				String processingTime = vars.get("processing_time");
				String productId = req.params(":productId");

				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);


				message = SellerActions.setProductInfo(productId, title, quantity, processingTime);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_picture/:productId/:pictureNum", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String url = vars.get("picture_url");
				String productId = req.params(":productId");
				String pictureNum = req.params(":pictureNum");


				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.savePicture(productId, pictureNum, url);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_bullet/:productId/:bulletNum", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String bullet = vars.get("bullet");
				String productId = req.params(":productId");
				String bulletNum = req.params(":bulletNum");


				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.saveBullet(productId, bulletNum, bullet);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_shipping/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				log.info("vars = " + vars.toString());
				String productId = req.params(":productId");

				String fromCountryId = vars.get("from_country");

				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.saveShipping(productId, fromCountryId);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_shipping_cost/:productId/:shippingNum", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				log.info("vars = " + vars.toString());
				String productId = req.params(":productId");
				String shippingNum = req.params(":shippingNum");

				String toCountryId = vars.get("to_country");
				String price = vars.get("shipping_cost");
				String nativeCurrId = vars.get("currency");


				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.saveShippingCost(productId, shippingNum, toCountryId,
						price, nativeCurrId);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/set_product_physical/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				log.info("vars = " + vars.toString());
				String productId = req.params(":productId");
				
				String isPhysical = vars.get("is_physical");



				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.saveShippingPhysical(productId, isPhysical);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/delete_product_shipping_cost/:productId/:shippingNum", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");
				String shippingNum = req.params(":shippingNum");


				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.deleteShippingCost(productId, shippingNum);


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/delete_product_bullet/:productId/:bulletNum", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");
				String bulletNum = req.params(":bulletNum");


				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.deleteBullet(productId, bulletNum);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/delete_picture/:productId/:pictureNum", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");
				String pictureNum = req.params(":pictureNum");


				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = SellerActions.deletePicture(productId, pictureNum);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_details/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String summerNoteHtml = req.body();
				String productId = req.params(":productId");

				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);


				message = SellerActions.saveProductDetails(productId, summerNoteHtml);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_price/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				//				{price_1=3, price=21, variable_price=1, is_auction=1, 
				//						expiration_time=2015-03-19, price_currency_iso={{currency.iso}}, 
				//						start_amount_auction=3, price_select=1}

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				log.info(vars.toString());

				String productId = req.params(":productId");



				String price = vars.get("price");
				String currIso = vars.get("currency");
				String variablePrice = vars.get("check_variable_price");
				String priceSelect = vars.get("check_price_select");
				String price1 = vars.get("price_1");
				String price2 = vars.get("price_2");
				String price3 = vars.get("price_3");
				String price4 = vars.get("price_4");
				String price5 = vars.get("price_5");

				String isAuction = vars.get("is_auction");

				String exprStr = vars.get("expiration_time");
				String auctionExpirationDate = (exprStr != null) ? Long.toString(Tools.DTF3.parseMillis(exprStr
						)) : null;
				String auctionStartPrice = vars.get("start_amount_auction");
				String auctionReservePrice = vars.get("reserve_amount_auction");


				Tools.dbInit();
				//				
				String message = null;

				SellerActions.ensureSellerOwnsProduct(req, productId);
				// First set the product price and product price list
				message = SellerActions.saveProductPrice(productId, price, currIso,
						variablePrice, priceSelect, price1, price2, price3, price4, price5);

				// Now set the auction info
				if (isAuction != null) {
					SellerActions.saveAuction(productId, isAuction, auctionExpirationDate,
							auctionStartPrice, auctionReservePrice, currIso);
				}
				
				
			
				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/set_product_category/:productId/:productCategory", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");
				String productCategory = req.params(":productCategory");

				Tools.dbInit();

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);


				message = SellerActions.saveProductCategory(productId, productCategory);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/create_product_review/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");

				Tools.dbInit();

				String reviewId = null;

				User user = UserActions.getUserFromSessionId(req);

				Review r = UserActions.createProductReview(productId, 
						user.getId().toString());

				reviewId = r.getId().toString();



				return reviewId;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});


		post("/save_product_review/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				log.info(vars.toString());

				String productId = req.params(":productId");

				String stars = vars.get("stars");
				String headline = vars.get("headline");
				String textHtml = vars.get("text_html");



				Tools.dbInit();
				//				
				String message = null;

				User user = UserActions.getUserFromSessionId(req);

				message = UserActions.saveProductReview(productId, 
						user.getId().toString(), 
						stars, 
						headline, 
						textHtml);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/delete_product_review/:reviewId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String reviewId = req.params(":reviewId");

				Tools.dbInit();

				String message = null;

				User user = UserActions.getUserFromSessionId(req);

				message = UserActions.deleteProductReview(reviewId, user.getId().toString());


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/review_vote/:reviewId/:vote", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String reviewId = req.params(":reviewId");
				String vote = req.params(":vote");

				Tools.dbInit();

				String message = null;

				User user = UserActions.getUserFromSessionId(req);

				message = UserActions.voteOnReview(reviewId, user.getId().toString(), vote);


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/question_vote/:questionId/:vote", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String questionId = req.params(":questionId");
				String vote = req.params(":vote");

				Tools.dbInit();

				String message = null;

				User user = UserActions.getUserFromSessionId(req);

				message = UserActions.voteOnQuestion(questionId, user.getId().toString(), vote);


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/answer_vote/:answerId/:vote", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String answerId = req.params(":answerId");
				String vote = req.params(":vote");

				Tools.dbInit();

				String message = null;

				User user = UserActions.getUserFromSessionId(req);

				message = UserActions.voteOnAnswer(answerId, user.getId().toString(), vote);


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/ask_question/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				log.info("vars = " + vars.toString());
				String productId = req.params(":productId");

				String text = vars.get("question");


				Tools.dbInit();

				User user = UserActions.getUserFromSessionId(req);

				String message = null;
				SellerActions.ensureSellerOwnsProduct(req, productId);

				message = UserActions.askQuestion(productId, user.getId().toString(), text);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/answer_question/:questionId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				log.info("vars = " + vars.toString());
				String questionId = req.params(":questionId");

				String answer = vars.get("answer");


				Tools.dbInit();

				User user = UserActions.getUserFromSessionId(req);

				String message = null;

				message = UserActions.answerQuestion(questionId, user.getId().toString(), answer);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/add_to_cart/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");

				Tools.dbInit();

				User user = UserActions.getUserFromSessionId(req);

				String json = null;
			

				json = UserActions.addToCart(user.getId().toString(), productId);


				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/remove_from_cart/:cartItemId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String cartItemId = req.params(":cartItemId");

				Tools.dbInit();

				User user = UserActions.getUserFromSessionId(req);

				String json = null;
			

				json = UserActions.removeFromCart(user.getId().toString(), cartItemId);


				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/add_to_wishlist/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");

				Tools.dbInit();

				User user = UserActions.getUserFromSessionId(req);

				String json = null;
			

				json = UserActions.addToWishlist(user.getId().toString(), productId);


				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/remove_from_wishlist/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");

				Tools.dbInit();

				User user = UserActions.getUserFromSessionId(req);

				String json = null;
			

				json = UserActions.removeFromWishlist(user.getId().toString(), productId);


				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		
		post("/save_address", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				

				
				String fullName = vars.get("full_name");
				String street = vars.get("street");
				String addrTwo = vars.get("addr_two");
				String zipcode = vars.get("zipcode");
				String city = vars.get("city");
				String state = vars.get("state");
				String countryId = vars.get("country"); // need to check on this

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);
				
				String message = UserActions.saveAddress(user.getId().toString(),
						fullName,
						street,
						addrTwo,
						city,
						state,
						zipcode,
						countryId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/edit_address/:addressId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				
				String addressId = req.params(":addressId");
				
				String fullName = vars.get("full_name");
				String street = vars.get("street");
				String addrTwo = vars.get("addr_two");
				String zipcode = vars.get("zipcode");
				String city = vars.get("city");
				String state = vars.get("state");
				String countryId = vars.get("country"); // need to check on this

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);
				
				String message = UserActions.editAddress(user.getId().toString(),
						addressId,
						fullName,
						street,
						addrTwo,
						city,
						state,
						zipcode,
						countryId);
				



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/save_shipment/:addressId/:sellerId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String addressId = req.params(":addressId");
				String sellerId = req.params(":sellerId");
				
				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);
				
				String message = UserActions.saveShipment(user.getId().toString(),
						addressId, sellerId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/create_payment/:sellerId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String sellerId = req.params(":sellerId");
				
				Tools.dbInit();
				com.bitmerchant.tools.Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);
				
				String message = UserActions.createPayment(user.getId().toString(),
						sellerId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
				com.bitmerchant.tools.Tools.dbClose();
			}

		});
		
		/** 
		 * This is a successful payment
		 */
		post("/callback/:paymentId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String paymentId = req.params(":paymentId");
				
				Tools.dbInit();		
				log.info("i got the callback");
				PaymentActions.updatePayment(paymentId);
				UserActions.createFeedbackFromPaymentSuccess(paymentId);
				
				return null;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		
		
		
		post("/delete_address/:addressId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String addressId = req.params(":addressId");

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);
				
				String message = UserActions.deleteAddress(user.getId().toString(),addressId);
				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/save_feedback/:feedbackId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				log.info(vars.toString());

				String feedbackId = req.params(":feedbackId");

				String stars = vars.get("stars");
				String arrivedOnTime = vars.get("arrived_on_time");
				String correctlyDescribed = vars.get("correctly_described");
				String promptService = vars.get("prompt_service");
				String comments = vars.get("comments");

				Tools.dbInit();
						
				String message = null;

				User user = UserActions.getUserFromSessionId(req);

				message = UserActions.saveFeedback(feedbackId, 
						user.getId().toString(), 
						stars, 
						arrivedOnTime, 
						correctlyDescribed,
						promptService,
						comments);




				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		

		get("/get_review_vote/:reviewId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				String reviewId = req.params(":reviewId");

				Tools.dbInit();

				String json = null;

				User user = UserActions.getUserFromSessionId(req);

				ReviewVote rv = REVIEW_VOTE.findFirst("user_id = ? and id = ?",
						user.getId().toString(),
						reviewId);

				if (rv != null) {
					json = rv.toJson(false); }
				else {
					json = "[]";
				}



				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_question_vote/:questionId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				String questionId = req.params(":questionId");

				Tools.dbInit();

				String json = null;

				User user = UserActions.getUserFromSessionId(req);

				QuestionVote rv = QUESTION_VOTE.findFirst("user_id = ? and id = ?",
						user.getId().toString(),
						questionId);

				if (rv != null) {
					json = rv.toJson(false); }
				else {
					json = "[]";
				}



				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_answer_vote/:answerId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				String answerId = req.params(":answerId");

				Tools.dbInit();

				String json = null;

				User user = UserActions.getUserFromSessionId(req);

				AnswerVote rv = ANSWER_VOTE.findFirst("user_id = ? and id = ?",
						user.getId().toString(),
						answerId);
				
				if (rv != null) {
					json = rv.toJson(false); }
				else {
					json = "[]";
				}



				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_reviews/:productId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String productId = req.params(":productId");
				Tools.dbInit();

				String json = null;

				List<ReviewView> rvs = REVIEW_VIEW.find("product_id = ?", productId);

				json = Tools.nodeToJson(Transformations.reviewViewJson(rvs));

				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});



		get("/get_category/:parentId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String parentId = req.params(":parentId");

				Tools.dbInit();
				String json;
				if (parentId.equals("null")) {
					parentId = null;
					json = CATEGORY.find("parent is ?", parentId).toJson(false, "id", "name", "parent");
				} else {
					json = CATEGORY.find("parent = ?", parentId).toJson(false, "id", "name", "parent");
				}

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_category_tree/:id", (req, res) -> {

			try {
				String id = req.params(":id");
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				String json = CategoryActions.getCategoryTree(id);

				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/time_spans", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				String json = TIME_SPAN_VIEW.findAll().toJson(false);

				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		get("/countries", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				String json = COUNTRY.findAll().toJson(false);

				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});

		get("/currencies", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				String json = CURRENCY.findAll().toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		get("/get_user", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);

				String json = user.toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});

		get("/get_seller", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				Seller s = SellerActions.getSellerFromSessionId(req);

				String json = s.toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});

		get("/get_product_bullets/:productId", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				String productId = req.params(":productId");
				Tools.dbInit();
				String json = PRODUCT_BULLET.where("product_id = ?", productId).
						orderBy("num_ asc").toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_product_pictures/:productId", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				String productId = req.params(":productId");
				Tools.dbInit();
				String json = PRODUCT_PICTURE.where("product_id = ?", productId).
						orderBy("num_ asc").toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_shipping/:productId", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				String productId = req.params(":productId");
				Tools.dbInit();

				Shipping s = SHIPPING.findFirst("product_id = ?", productId);

				String json;
				if (s != null) {
					json = s.toJson(false);
				} else {
					json = "[]";
				}

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_shipping_costs/:productId", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				String productId = req.params(":productId");
				Tools.dbInit();

				String json;
				Shipping s = SHIPPING.findFirst("product_id = ?", productId);
				if (s != null) {
					json = SHIPPING_COST.where("shipping_id = ?", s.getId().toString()).toJson(false);
				} else {
					json = "[]";
				}


				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/product_thumbnails", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				Seller seller = SellerActions.getSellerFromSessionId(req);

				List<ProductThumbnailView> pvs = PRODUCT_THUMBNAIL_VIEW.where(
						"seller_id = ?", seller.getId().toString());

				String json = Tools.nodeToJson(
						Transformations.productThumbnailViewJson(pvs));



				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});
		
		get("/wishlist_thumbnails", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);
				
				
				String json = UserActions.getWishlistThumbnails(user.getId().toString());
				log.info(json);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});
		
		get("/get_category_thumbnails/:categoryId", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				String categoryId = req.params(":categoryId");
				
				String json = CategoryActions.getCategoryThumbnails(categoryId);
				log.info(json);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});
		
		get("/get_shop_thumbnails/:sellerId", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				String sellerId = req.params(":sellerId");
				List<ProductThumbnailView> pvs = PRODUCT_THUMBNAIL_VIEW.find("seller_id = ?", sellerId);
			
				String json = Tools.nodeToJson(Transformations.productThumbnailViewJson(pvs));

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});
		
		get("/get_trending_thumbnails/:sortType", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				
				
				String sortType = URLDecoder.decode(req.params(":sortType"),"UTF-8");
				log.info("sort type = " + sortType);
			
				List<ProductThumbnailView> pvs = PRODUCT_THUMBNAIL_VIEW.findAll().limit(3).orderBy(sortType);
			
				String json = Tools.nodeToJson(Transformations.productThumbnailViewJson(pvs));

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});

		get("/get_product/:productId", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				String productId = req.params(":productId");

				ProductView pv = PRODUCT_VIEW.findFirst(
						"id = ?", productId);

				String json = Tools.nodeToJson(
						Transformations.productViewJson(pv));



				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});

		get("/get_your_reviews", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);

				List<ReviewView> rvs = REVIEW_VIEW.where("user_id = ?", user.getId().toString())
						.orderBy("created_at desc");


				String json = Tools.nodeToJson(
						Transformations.yourReviewsViewJson(rvs));



				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});
		
		get("/get_your_feedback", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				User user = UserActions.getUserFromSessionId(req);

				List<FeedbackView> fvs = FEEDBACK_VIEW.where("user_id = ?", user.getId().toString())
						.orderBy("created_at desc");


				String json = Tools.nodeToJson(
						Transformations.yourFeedbackViewJson(fvs));



				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}
		});
		
		get("/get_cart", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				User user = UserActions.getUserFromSessionId(req);
				
				String json = null;
				
				// get the cart items
				LazyList<CartView> cvs = CART_VIEW.find("user_id = ?", user.getId().toString());
				if (cvs == null) {
					json = "[]";
				} else {
					json = cvs.toJson(false);
				}
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_cart_grouped", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				User user = UserActions.getUserFromSessionId(req);
				
				String json = null;
				
				json = Tools.nodeToJson(Transformations.cartGroupedJson(user.getId().toString()));
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_cart_grouped/:sellerId", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				User user = UserActions.getUserFromSessionId(req);
				String sellerId = req.params(":sellerId");
				
				String json = null;
				
				json = Tools.nodeToJson(Transformations.cartGroupedJson(
						user.getId().toString(),sellerId));
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_orders_grouped", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				User user = UserActions.getUserFromSessionId(req);
				String view = (req.queryParams("view") != null) ? req.queryParams("view") : "all";
				
				
				String json = null;
				
				json = Tools.nodeToJson(Transformations.orderGroupedJson(user.getId().toString(), view));
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_addresses", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				User user = UserActions.getUserFromSessionId(req);
				
				String json = null;
				
				json = ADDRESS_VIEW.find("user_id = ?", user.getId().toString()).toJson(false);
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_browse", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				String json = Tools.nodeToJson(Transformations.browseJson());
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_top_categories", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				// Finds the top level categories
				String json = CATEGORY.find("parent is null").toJson(false);
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_subcategories/:categoryId", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
				
				String categoryId = req.params(":categoryId");
				
				String json = CATEGORY.find("parent = ? or id = ?", categoryId, categoryId).toJson(false);
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/get_payment/:paymentId", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
								
				String paymentId = req.params(":paymentId");
				String json = null;
				
				json = PAYMENT.findFirst("id = ?", paymentId).toJson(false);
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("/category_search/:query", (req, res) -> {
			
			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
								
				String query = req.params(":query");
				String json = null;
				
				json = CATEGORY.find("name like ?", "%" + query + "%").toJson(false);
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});
		
		get("/product_search/:query", (req, res) -> {
			
			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
								
				String query = req.params(":query");
				String json = null;
				
				List<ProductThumbnailView> pvs = 
						PRODUCT_THUMBNAIL_VIEW.find("title like ?", "%" + query + "%");
				json = Transformations.searchProductThumbnailViewJson(pvs);
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});
		
		get("/shop_search/:query", (req, res) -> {
			
			try {
				Tools.allowAllHeaders(req, res);
				Tools.dbInit();
								
				String query = req.params(":query");
				String json = null;
				
				json = SELLER.find("shop_name like ?", "%" + query + "%").toJson(false);
				
				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});



		// All the webpages
		
		
		get("/shop/:sellerId", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return Tools.readFile(DataSources.PAGES("shop"));
		});
		
		get("/category/:categoryId", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return Tools.readFile(DataSources.PAGES("category"));
		});
		
		
		get("/product/edit/:productId", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return Tools.readFile(DataSources.PAGES("product_edit"));
		});

		get("/product/review/:productId", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return Tools.readFile(DataSources.PAGES("review"));
		});

		get("/product/:productId", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return Tools.readFile(DataSources.PAGES("product"));
		});
		
		get("/checkout/:sellerId", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return Tools.readFile(DataSources.PAGES("checkout"));
		});






		get("/", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);
			return Tools.readFile(DataSources.PAGES("home"));
		});
	}
}
