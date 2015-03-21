package com.openmarket.webservice;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openmarket.db.Tables.AnswerVote;
import com.openmarket.db.Tables.Category;
import com.openmarket.db.Tables.Country;
import com.openmarket.db.Tables.Currency;
import com.openmarket.db.Tables.Product;
import com.openmarket.db.Tables.ProductBullet;
import com.openmarket.db.Tables.ProductPicture;
import com.openmarket.db.Tables.ProductThumbnailView;
import com.openmarket.db.Tables.ProductView;
import com.openmarket.db.Tables.QuestionVote;
import com.openmarket.db.Tables.Review;
import com.openmarket.db.Tables.ReviewView;
import com.openmarket.db.Tables.ReviewVote;
import com.openmarket.db.Tables.Seller;
import com.openmarket.db.Tables.Shipping;
import com.openmarket.db.Tables.ShippingCost;
import com.openmarket.db.Tables.TimeSpanView;
import com.openmarket.db.Tables.User;
import com.openmarket.db.Transformations;
import com.openmarket.db.actions.Actions.CategoryActions;
import com.openmarket.db.actions.Actions.SellerActions;
import com.openmarket.db.actions.Actions.UserActions;
import com.openmarket.tools.DataSources;
import com.openmarket.tools.Tools;

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

				Seller seller = SellerActions.getSellerFromSessionId(req);

				String message = SellerActions.saveShopName(seller, shopName);



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
				SellerActions.ensureSellerOwnsProduct(req, questionId);

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

		get("/get_review_vote/:reviewId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				String reviewId = req.params(":reviewId");

				Tools.dbInit();

				String json = null;

				User user = UserActions.getUserFromSessionId(req);

				ReviewVote rv = ReviewVote.findFirst("user_id = ? and id = ?",
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

				QuestionVote rv = QuestionVote.findFirst("user_id = ? and id = ?",
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

				AnswerVote rv = AnswerVote.findFirst("user_id = ? and id = ?",
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

				List<ReviewView> rvs = ReviewView.find("product_id = ?", productId);

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



		get("/category/:parentId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				String parentId = req.params(":parentId");

				Tools.dbInit();
				String json;
				if (parentId.equals("null")) {
					parentId = null;
					json = Category.find("parent is ?", parentId).toJson(false, "id", "name", "parent");
				} else {
					json = Category.find("parent = ?", parentId).toJson(false, "id", "name", "parent");
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

		get("/category_tree/:id", (req, res) -> {

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
				String json = TimeSpanView.findAll().toJson(false);

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
				String json = Country.findAll().toJson(false);

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
				String json = Currency.findAll().toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});


		//		get("/get_product/:productId", (req, res) -> { 
		//			Tools.allowAllHeaders(req, res);
		//			Tools.dbInit();
		//			String productId = req.params(":productId");
		//			String json = ProductView.where("id = ?", productId).toJson(false);
		//			Tools.dbClose();
		//			return json;
		//
		//
		//		});

		get("/get_product_bullets/:productId", (req, res) -> { 
			try {
				Tools.allowAllHeaders(req, res);
				String productId = req.params(":productId");
				Tools.dbInit();
				String json = ProductBullet.where("product_id = ?", productId).
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
				String json = ProductPicture.where("product_id = ?", productId).
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

				Shipping s = Shipping.findFirst("product_id = ?", productId);

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
				Shipping s = Shipping.findFirst("product_id = ?", productId);
				if (s != null) {
					json = ShippingCost.where("shipping_id = ?", s.getId().toString()).toJson(false);
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

				List<ProductThumbnailView> pvs = ProductThumbnailView.where(
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

		get("/get_product/:productId", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				String productId = req.params(":productId");

				ProductView pv = ProductView.findFirst(
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

				List<ReviewView> rvs = ReviewView.where("user_id = ?", user.getId().toString())
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



		// All the webpages
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



		// All the simple webpages
		get("/:page", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			String pageName = req.params(":page");
			return Tools.readFile(DataSources.PAGES(pageName));
		});


		get("/", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);
			return Tools.readFile(DataSources.PAGES("home"));
		});
	}
}
