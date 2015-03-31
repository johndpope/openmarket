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
}
