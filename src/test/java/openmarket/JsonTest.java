package openmarket;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.openmarket.db.Tables.ProductPicture;
import com.openmarket.db.Tables.ProductThumbnailView;
import com.openmarket.db.Tables.ProductView;
import com.openmarket.tools.Tools;

public class JsonTest extends TestCase {

	public void testThumbnailJson() throws JsonGenerationException, JsonMappingException, IOException {


//String s="CREATE VIEW product_thumbnail_view AS \n"+
//"SELECT \n"+
//"product.id as product_id,\n"+
//"title,\n"+
//"seller_id,\n"+
//"shop_name, \n"+
//"count(review.id) as number_of_reviews,\n"+
//"auction,\n"+
//"CASE WHEN auction=\'1\'\n"+
//"\tTHEN (select max(bid.amount) from bid where bid.auction_id = auction.id) \n"+
//"\tELSE max(price) \n"+
//"END as price, \n"+
//"currency.iso as price_iso \n"+
//"FROM product \n"+
//"inner join currency on product_price.native_currency_id = currency.id \n"+
//"left join seller on product.seller_id = seller.id \n"+
//"left join auction on product.id = auction.product_id \n"+
//"left join review on review.product_id = product.id \n"+
//"left join product_price on product_price.product_id = product.id \n"+
//"group by product.id\n"+
//";";
//			
//		Tools.writeRQL(s);
		
	}
	
	
	

}
