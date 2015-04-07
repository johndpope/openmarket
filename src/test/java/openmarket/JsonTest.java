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



		String s2="DROP VIEW order_group;";
		Tools.writeRQL(s2);




		String s="CREATE VIEW order_group AS \nselect cart_item.user_id,\nseller_id, \nshop_name, \nmax(time_span_string) as time_span_string, \nsum(product_price.price*cart_item.quantity) as cost,\nmax(iso) as iso,\nIFNULL(max(shipping_cost.price),0) as shipping,\nsum(product_price.price*cart_item.quantity) + IFNULL(max(shipping_cost.price),0) as checkout_total ,\nshipment_id, \nshipment.tracking_url, \naddress.full_name,\naddress.address_line_1,\naddress.address_line_2,\naddress.city,\naddress.state,\naddress.zip,\naddress.country_id,\npayment_id,\npurchased, \ncompleted, \norder_iframe,\npayment.created_at \nfrom cart_item \nleft join product on product.id = cart_item.product_id \nleft join product_price on cart_item.product_id = product_price.product_id \nleft join shipping on cart_item.product_id = shipping.product_id \nleft join shipping_cost on shipping.id = shipping_cost.shipping_id \nleft join seller on product.seller_id = seller.id \nleft join time_span_view on product.processing_time_span_id = time_span_view.id \nleft join currency on product_price.native_currency_id = currency.id \nleft join shipment on cart_item.shipment_id = shipment.id \nleft join address on shipment.address_id = address.id \nleft join payment on cart_item.payment_id = payment.id \nwhere purchased = 1 \ngroup by cart_item.user_id, payment_id \n;";
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
