package openmarket;

import java.io.IOException;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.openmarket.db.Transformations;
import com.openmarket.tools.Tools;

import static com.openmarket.db.Tables.*;

public class JsonTest extends TestCase {

	public void testThumbnailJson() throws JsonGenerationException, JsonMappingException, IOException {



		String s2="drop view product_thumbnail_view; drop view product_view;";
		Tools.writeRQL(s2);




		String s="CREATE VIEW product_view AS \nSELECT product.id,\nseller_id,\nshop_name,\ncategory_id,\nbuy,\nauction,\nproduct.quantity,\ntitle,\nprocessing_time_span_id,\ntime_span_string,\nprice,\nnative_currency_id,\ncurrency.iso as price_iso,\nvariable_price,\nprice_select,\nprice_1,\nprice_2,\nprice_3,\nprice_4,\nprice_5,\nexpire_time,\nstart_amount,\nreserve_amount,\nphysical,\ncurrency_id as auction_currency_id,\nauction_currency.iso as auction_currency_iso,\nshipping.id as shipping_id, \nfrom_country_id, \nfrom_country.name as from_country, \ncount(distinct review.id) as number_of_reviews, \nifnull(avg(review.stars),0) as review_avg, \ncount(distinct cart_item.id) as number_of_purchases,\nproduct_html \nFROM product \nleft join time_span_view on product.processing_time_span_id = time_span_view.id \nleft join product_page on product.id = product_page.product_id \nleft join product_price on product.id = product_price.product_id \nleft join auction on product.id = auction.product_id \nleft join currency on product_price.native_currency_id = currency.id \nleft join currency as auction_currency on auction.currency_id = auction_currency.id \nleft join shipping on product.id = shipping.product_id \nleft join country as from_country on shipping.from_country_id = from_country.id \nleft join review on review.product_id = product.id \nleft join seller on product.seller_id = seller.id \nleft join cart_item on product.id = cart_item.product_id and purchased=1 \ngroup by product.id\n;\n\nCREATE VIEW product_thumbnail_view AS \nSELECT \nproduct.id as product_id,\ntitle,\nseller_id,\ncategory_id,\nshop_name, \ncount(distinct review.id) as number_of_reviews, \nifnull(avg(review.stars),0) as review_avg, \ncount(distinct cart_item.id) as number_of_purchases,\nauction,\nCASE WHEN auction=\'1\'\n\tTHEN (select max(bid.amount) from bid where bid.auction_id = auction.id) \n\tELSE max(price) \nEND as price, \ncurrency.iso as price_iso \nFROM product \ninner join currency on product_price.native_currency_id = currency.id \nleft join seller on product.seller_id = seller.id \nleft join auction on product.id = auction.product_id \nleft join review on review.product_id = product.id \nleft join product_price on product_price.product_id = product.id \nleft join cart_item on product.id = cart_item.product_id and purchased=1 \ngroup by product.id \n;";
		Tools.writeRQL(s);



	}




}
