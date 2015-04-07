package com.openmarket.db;

import static com.openmarket.db.Tables.AnswerView;
import static com.openmarket.db.Tables.ProductBullet;
import static com.openmarket.db.Tables.ProductPicture;
import static com.openmarket.db.Tables.ProductThumbnailView;
import static com.openmarket.db.Tables.QuestionView;
import static com.openmarket.db.Tables.ReviewComment;
import static com.openmarket.db.Tables.ReviewView;
import static com.openmarket.db.Tables.ShippingCostView;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


import com.openmarket.tools.Tools;

import static com.openmarket.db.Tables.*;
public class Transformations {

	public static ObjectNode productThumbnailViewJson(ProductThumbnailView pv) {


		ObjectNode a = Tools.MAPPER.createObjectNode();

		JsonNode c = Tools.jsonToNode(pv.toJson(false));

		List<ProductPicture> pps = ProductPicture.find("product_id = ?", pv.getString("product_id"));

		ObjectNode on = Tools.MAPPER.valueToTree(c);
		a.putAll(on);

		ArrayNode an = a.putArray("pictures");

		for (ProductPicture pp : pps) {
			an.add(Tools.jsonToNode(pp.toJson(false)));
		}


		return a;
	}

	public static ObjectNode productThumbnailViewJson(List<ProductThumbnailView> pvs) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode ab = a.putArray("products");

		for (ProductThumbnailView pv : pvs) {
			ab.add(productThumbnailViewJson(pv));

		}

		return a;
	}

	public static ObjectNode productViewJson(ProductView pv) {

		String productId = pv.getString("id");


		ObjectNode a = Tools.MAPPER.createObjectNode();

		JsonNode c = Tools.jsonToNode(pv.toJson(false));

		ObjectNode on = Tools.MAPPER.valueToTree(c);
		a.putAll(on);



		// add the pictures
		List<ProductPicture> pps = ProductPicture.find("product_id = ?", productId);

		ArrayNode an = a.putArray("pictures");

		for (ProductPicture pp : pps) {
			an.add(Tools.jsonToNode(pp.toJson(false)));
		}

		// Add the bullets
		List<ProductBullet> pbs = ProductBullet.find("product_id = ?", productId);

		ArrayNode ab = a.putArray("bullets");

		for (ProductBullet pp : pbs) {
			ab.add(Tools.jsonToNode(pp.toJson(false)));
		}

		// Add shipping info
		List<ShippingCostView> scs = ShippingCostView.find("shipping_id = ?", pv.getString("shipping_id"));

		ArrayNode as = a.putArray("shipping_costs");

		for (ShippingCostView sc : scs) {
			as.add(Tools.jsonToNode(sc.toJson(false)));
		}


		// Add 3 reviews
		List<ReviewView> rvs = ReviewView.where("product_id = ?", productId).limit(3).
				orderBy("votes_sum desc");
		ObjectNode reviewNodes = reviewViewJson(rvs);

		a.putAll(reviewNodes);

		// Add 3 questions
		List<QuestionView> qvs = QuestionView.where("product_id = ?", productId).limit(3).
				orderBy("votes_sum desc");
		ObjectNode questionNodes = questionViewJson(qvs);

		a.putAll(questionNodes);

		// Add 4 other sellers products
		List<ProductThumbnailView> otherProducts = 
				ProductThumbnailView.find("seller_id = ? and product_id != ?", 
						pv.getString("seller_id"),
						pv.getId().toString()).limit(4);

		ObjectNode otherProductsNode = productThumbnailViewJson(otherProducts);

		a.putAll(otherProductsNode);

		return a;

	}

	public static ObjectNode reviewViewJson(List<ReviewView> rvs) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode ab = a.putArray("reviews");

		for (ReviewView rv : rvs) {
			ab.add(reviewViewJson(rv));
		}

		return a;

	}

	public static ObjectNode reviewViewJson(ReviewView rv) {


		ObjectNode a = Tools.MAPPER.createObjectNode();

		JsonNode c = Tools.jsonToNode(rv.toJson(false));

		List<ReviewComment> rcs = ReviewComment.find("review_id = ?", rv.getString("id"));

		ObjectNode on = Tools.MAPPER.valueToTree(c);
		a.putAll(on);

		ArrayNode an = a.putArray("comments");

		for (ReviewComment rc : rcs) {
			an.add(Tools.jsonToNode(rc.toJson(false)));
		}


		return a;
	}

	public static ObjectNode yourReviewsViewJson(ReviewView rv) {

		JsonNode c = Tools.jsonToNode(rv.toJson(false));

		ObjectNode on = Tools.MAPPER.valueToTree(c);

		ProductThumbnailView ptv = ProductThumbnailView.findFirst
				("product_id = ?", rv.getString("product_id"));
		ObjectNode pvj = productThumbnailViewJson(ptv);

		on.put("thumbnail", pvj);

		return on;
	}

	public static ObjectNode yourReviewsViewJson(List<ReviewView> rvs) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode ab = a.putArray("reviews");

		for (ReviewView rv : rvs) {
			ab.add(yourReviewsViewJson(rv));
		}

		return a;

	}

	public static ObjectNode questionViewJson(List<QuestionView> qvs) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode ab = a.putArray("questions");

		for (QuestionView qv : qvs) {
			ab.add(questionViewJson(qv));
		}

		return a;

	}

	public static ObjectNode questionViewJson(QuestionView qv) {


		ObjectNode a = Tools.MAPPER.createObjectNode();

		JsonNode c = Tools.jsonToNode(qv.toJson(false));

		List<AnswerView> rcs = AnswerView.find("question_id = ?", qv.getString("id")).
				orderBy("votes_sum desc");

		ObjectNode on = Tools.MAPPER.valueToTree(c);
		a.putAll(on);

		ArrayNode an = a.putArray("answers");

		for (AnswerView rc : rcs) {
			an.add(Tools.jsonToNode(rc.toJson(false)));
		}


		return a;
	}

	public static ObjectNode cartGroupedJson(String userId) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode ab = a.putArray("cart_groups");
		
		
		List<CartGroup> cgs = CartGroup.find("user_id = ?", userId);

		for (CartGroup cg : cgs) {
			// each row is a distinct seller and payment?
			String sellerId = cg.getString("seller_id");

			List<CartView> cvs = CartView.find("user_id = ? and seller_id = ?",
					userId, sellerId);


			JsonNode cgNode = Tools.jsonToNode(cg.toJson(false));

			// Need to add the product array to this later
			ObjectNode on = Tools.MAPPER.valueToTree(cgNode);		

			ArrayNode ac = on.putArray("products");
			for (CartView cv : cvs) {
				ac.add(Tools.jsonToNode(cv.toJson(false)));

			}
			ab.add(on);


		}

		return a;



	}

	public static ObjectNode cartGroupedJson(String userId, String sellerId) {

		CartGroup cg = CartGroup.findFirst("user_id = ? and seller_id = ?", userId, sellerId);

		List<CartView> cvs = CartView.find("user_id = ? and seller_id = ?",
				userId, sellerId);


		JsonNode cgNode = Tools.jsonToNode(cg.toJson(false));

		// Need to add the product array to this later
		ObjectNode on = Tools.MAPPER.valueToTree(cgNode);		

		ArrayNode ac = on.putArray("products");
		for (CartView cv : cvs) {
			ac.add(Tools.jsonToNode(cv.toJson(false)));

		}

		return on;



	}
	
	public static ObjectNode orderGroupedJson(String userId, String view) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode ab = a.putArray("order_groups");
		
		List<OrderGroup> ogs;
		
		if (view.equals("open")) {
			ogs = OrderGroup.find("user_id = ? and tracking_url is null", userId).orderBy("created_at desc");
		} else {
			ogs = OrderGroup.find("user_id = ?", userId).orderBy("created_at desc");
		}
		for (OrderGroup og : ogs) {
			// each row is a distinct seller and payment?
			String paymentId = og.getString("payment_id");

			List<OrderView> cvs = OrderView.find("user_id = ? and payment_id = ?",
					userId, paymentId);


			JsonNode cgNode = Tools.jsonToNode(og.toJson(false));

			// Need to add the product array to this later
			ObjectNode on = Tools.MAPPER.valueToTree(cgNode);		

			ArrayNode ac = on.putArray("products");
			for (OrderView cv : cvs) {
				ac.add(Tools.jsonToNode(cv.toJson(false)));

			}
			ab.add(on);


		}

		return a;



	}
	
}
