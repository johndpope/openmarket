package com.openmarket.db;

import static com.openmarket.db.Tables.ANSWER_VIEW;
import static com.openmarket.db.Tables.PRODUCT_BULLET;
import static com.openmarket.db.Tables.PRODUCT_PICTURE;
import static com.openmarket.db.Tables.PRODUCT_THUMBNAIL_VIEW;
import static com.openmarket.db.Tables.QUESTION_VIEW;
import static com.openmarket.db.Tables.REVIEW_COMMENT;
import static com.openmarket.db.Tables.REVIEW_VIEW;
import static com.openmarket.db.Tables.SHIPPING_COST_VIEW;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openmarket.db.Tables.ProductThumbnailView;
import com.openmarket.db.actions.Actions;
import com.openmarket.tools.Tools;

import static com.openmarket.db.Tables.*;
public class Transformations {

	static final Logger log = LoggerFactory.getLogger(Transformations.class);

	public static ObjectNode productThumbnailViewJson(ProductThumbnailView pv) {


		ObjectNode a = Tools.MAPPER.createObjectNode();

		JsonNode c = Tools.jsonToNode(pv.toJson(false));

		List<ProductPicture> pps = PRODUCT_PICTURE.find("product_id = ?", pv.getString("product_id"));

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
	
	public static String searchProductThumbnailViewJson(List<ProductThumbnailView> pvs) {
		if (pvs.size() == 0) {
			return "[]";
		}
		

		StringBuilder s = new StringBuilder();
		s.append("[");

		Iterator<ProductThumbnailView> pvIt = pvs.iterator();
		for (;;) {
			ProductThumbnailView pv = pvIt.next();
			s.append(productThumbnailViewJson(pv));
			
			if (pvIt.hasNext()) {
				s.append(",");
			} else {
				break;
			}

		}

		s.append("]");
		
		log.info(s.toString());
		
		return s.toString();
	}

	public static ObjectNode productViewJson(ProductView pv) {

		String productId = pv.getString("id");


		ObjectNode a = Tools.MAPPER.createObjectNode();

		JsonNode c = Tools.jsonToNode(pv.toJson(false));

		ObjectNode on = Tools.MAPPER.valueToTree(c);
		a.putAll(on);



		// add the pictures
		List<ProductPicture> pps = PRODUCT_PICTURE.find("product_id = ?", productId);

		ArrayNode an = a.putArray("pictures");

		for (ProductPicture pp : pps) {
			an.add(Tools.jsonToNode(pp.toJson(false)));
		}

		// Add the bullets
		List<ProductBullet> pbs = PRODUCT_BULLET.find("product_id = ?", productId);

		ArrayNode ab = a.putArray("bullets");

		for (ProductBullet pp : pbs) {
			ab.add(Tools.jsonToNode(pp.toJson(false)));
		}

		// Add shipping info
		List<ShippingCostView> scs = SHIPPING_COST_VIEW.find("shipping_id = ?", pv.getString("shipping_id"));

		ArrayNode as = a.putArray("shipping_costs");

		for (ShippingCostView sc : scs) {
			as.add(Tools.jsonToNode(sc.toJson(false)));
		}


		// Add 3 reviews
		List<ReviewView> rvs = REVIEW_VIEW.where("product_id = ?", productId).limit(3).
				orderBy("votes_sum desc");
		ObjectNode reviewNodes = reviewViewJson(rvs);

		a.putAll(reviewNodes);

		// Add 3 questions
		List<QuestionView> qvs = QUESTION_VIEW.where("product_id = ?", productId).limit(3).
				orderBy("votes_sum desc");
		ObjectNode questionNodes = questionViewJson(qvs);

		a.putAll(questionNodes);

		// Add 4 other sellers products
		List<ProductThumbnailView> otherProducts = 
				PRODUCT_THUMBNAIL_VIEW.find("seller_id = ? and product_id != ?", 
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

		List<ReviewComment> rcs = REVIEW_COMMENT.find("review_id = ?", rv.getString("id"));

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

		ProductThumbnailView ptv = PRODUCT_THUMBNAIL_VIEW.findFirst
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

	public static ObjectNode yourFeedbackViewJson(FeedbackView fv) {

		JsonNode c = Tools.jsonToNode(fv.toJson(false));

		ObjectNode on = Tools.MAPPER.valueToTree(c);

		ProductThumbnailView ptv = PRODUCT_THUMBNAIL_VIEW.findFirst
				("product_id = ?", fv.getString("product_id"));
		ObjectNode pvj = productThumbnailViewJson(ptv);

		on.put("thumbnail", pvj);

		return on;
	}

	public static ObjectNode yourFeedbackViewJson(List<FeedbackView> fvs) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode ab = a.putArray("feedback");

		for (FeedbackView fv : fvs) {
			ab.add(yourFeedbackViewJson(fv));
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

		List<AnswerView> rcs = ANSWER_VIEW.find("question_id = ?", qv.getString("id")).
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


		List<CartGroup> cgs = CART_GROUP.find("user_id = ?", userId);

		for (CartGroup cg : cgs) {
			// each row is a distinct seller and payment?
			String sellerId = cg.getString("seller_id");

			List<CartView> cvs = CART_VIEW.find("user_id = ? and seller_id = ?",
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

		CartGroup cg = CART_GROUP.findFirst("user_id = ? and seller_id = ?", userId, sellerId);

		List<CartView> cvs = CART_VIEW.find("user_id = ? and seller_id = ?",
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
			ogs = ORDER_GROUP.find("user_id = ? and tracking_url is null", userId).orderBy("created_at desc");
		} else {
			ogs = ORDER_GROUP.find("user_id = ?", userId).orderBy("created_at desc");
		}
		for (OrderGroup og : ogs) {
			// each row is a distinct seller and payment?
			String paymentId = og.getString("payment_id");

			List<OrderView> cvs = ORDER_VIEW.find("user_id = ? and payment_id = ?",
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

	public static ObjectNode browseJson() {

		List<BrowseView> bvs = BROWSE_VIEW.findAll();

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode lvl1 = a.putArray("level_1");

		ArrayNode lvl2 = null;

		String prevId = "";
		for (BrowseView bv : bvs) {
			String lvl1Id = bv.getString("id_1");
			String lvl1Name = bv.getString("name_1");

			String lvl2Id = bv.getString("id_2");
			String lvl2Name = bv.getString("name_2");

			log.info(lvl2Name);

			// first add the lvl 2s
			ObjectNode c = Tools.MAPPER.createObjectNode();
			c.put("id", lvl2Id);
			c.put("name", lvl2Name);


			ObjectNode d = Tools.MAPPER.createObjectNode();
			d.put("id", lvl1Id);
			d.put("name", lvl1Name);


			if (!prevId.equals(lvl1Id)) {
				lvl1.add(d);
				prevId = lvl1Id;
				lvl2 = d.putArray("level_2");
			}


			lvl2.add(c);

		}



		return a;

	}



}
