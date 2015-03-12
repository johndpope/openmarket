package com.openmarket.webservice;

import static spark.Spark.post;
import static spark.Spark.get;








import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;








import org.slf4j.Logger;
import org.slf4j.LoggerFactory;








import com.openmarket.db.Tables.Category;
import com.openmarket.db.Tables.Country;
import com.openmarket.db.Tables.Currency;
import com.openmarket.db.Tables.Product;
import com.openmarket.db.Tables.ProductBullet;
import com.openmarket.db.Tables.ProductPicture;
import com.openmarket.db.Tables.ProductPrice;
import com.openmarket.db.Tables.ProductView;
import com.openmarket.db.Tables.Seller;
import com.openmarket.db.Tables.TimeSpan;
import com.openmarket.db.Tables.TimeSpanView;
import com.openmarket.db.Tables.User;
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

				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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


				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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

				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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
				Tools.dbClose();


				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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

				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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
				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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


				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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


				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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


				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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

				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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

				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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


				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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
				String currIso = vars.get("price_currency_iso");
				String variablePrice = vars.get("variable_price");
				String priceSelect = vars.get("price_select");
				String price1 = vars.get("price_1");
				String price2 = vars.get("price_2");
				String price3 = vars.get("price_3");
				String price4 = vars.get("price_4");
				String price5 = vars.get("price_5");

				String isAuction = vars.get("is_auction");
				String auctionExpirationDate = vars.get("expiration_time");
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



				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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


				Tools.dbClose();

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
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
				Tools.dbClose();
				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			}

		});
		
		get("/category_tree/:id", (req, res) -> {
			
			String id = req.params(":id");
			Tools.allowAllHeaders(req, res);
			Tools.dbInit();
			String json = CategoryActions.getCategoryTree(id);
			Tools.dbClose();
			return json;
			
		});

		get("/time_spans", (req, res) -> { 
			Tools.allowAllHeaders(req, res);
			Tools.dbInit();
			String json = TimeSpanView.findAll().toJson(false);
			Tools.dbClose();
			return json;


		});

		get("/countries", (req, res) -> { 
			Tools.allowAllHeaders(req, res);
			Tools.dbInit();
			String json = Country.findAll().toJson(false);
			Tools.dbClose();
			return json;


		});

		get("/currencies", (req, res) -> { 
			Tools.allowAllHeaders(req, res);
			Tools.dbInit();
			String json = Currency.findAll().toJson(false);
			Tools.dbClose();
			return json;


		});


		get("/get_product/:productId", (req, res) -> { 
			Tools.allowAllHeaders(req, res);
			Tools.dbInit();
			String json = ProductView.findAll().toJson(false);
			Tools.dbClose();
			return json;


		});

		get("/get_product_bullets/:productId", (req, res) -> { 
			Tools.allowAllHeaders(req, res);
			String productId = req.params(":productId");
			Tools.dbInit();
			String json = ProductBullet.where("product_id = ?", productId).
					orderBy("num_ asc").toJson(false);
			Tools.dbClose();
			return json;

		});
		
		get("/get_product_pictures/:productId", (req, res) -> { 
			Tools.allowAllHeaders(req, res);
			String productId = req.params(":productId");
			Tools.dbInit();
			String json = ProductPicture.where("product_id = ?", productId).
					orderBy("num_ asc").toJson(false);
			Tools.dbClose();
			return json;

		});



		get("/product_edit/:productId", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			return Tools.readFile(DataSources.PAGES("product_edit"));
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
