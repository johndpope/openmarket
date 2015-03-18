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




String s="CREATE VIEW review_view AS \nselect *,\nSUM(case when vote = 1 then 1 else -1 end) as votes_sum,\ncount(review_vote.id) as votes_count \nfrom review \nleft join review_vote \non review.id = review_vote.review_id \ngroup by review.id \n;";
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
