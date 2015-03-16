package com.openmarket.db;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.openmarket.db.Tables.ProductPicture;
import com.openmarket.db.Tables.ProductThumbnailView;
import com.openmarket.tools.Tools;

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
}
