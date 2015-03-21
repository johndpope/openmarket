package openmarket;

import java.io.IOException;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.openmarket.db.Tables.ProductView;
import com.openmarket.db.Transformations;
import com.openmarket.tools.Tools;

public class JsonTest extends TestCase {

	public void testThumbnailJson() throws JsonGenerationException, JsonMappingException, IOException {



		String s2="Drop view product_thumbnail_view;";
		Tools.writeRQL(s2);


		String s="CREATE VIEW product_thumbnail_view AS \nSELECT \nproduct.id as product_id,\ntitle,\nseller_id,\nshop_name, \ncount(review.id) as number_of_reviews, \nifnull(avg(review.stars),0) as review_avg, \nauction,\nCASE WHEN auction=\'1\'\n\tTHEN (select max(bid.amount) from bid where bid.auction_id = auction.id) \n\tELSE max(price) \nEND as price, \ncurrency.iso as price_iso \nFROM product \ninner join currency on product_price.native_currency_id = currency.id \nleft join seller on product.seller_id = seller.id \nleft join auction on product.id = auction.product_id \nleft join review on review.product_id = product.id \nleft join product_price on product_price.product_id = product.id \ngroup by product.id \n;";
		Tools.writeRQL(s);

		Tools.dbInit();
		ProductView pv = ProductView.findFirst(
				"id = ?", 1);



		String json = Tools.nodeToJson(
				Transformations.productViewJson(pv));

		Tools.dbClose();

		System.out.println(json);
	}




}
