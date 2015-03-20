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



		String s2="Drop view question_view; DROP view answer_view;";
		Tools.writeRQL(s2);


String s="CREATE VIEW question_view AS \nselect question.id, question.user_id, product_id, text, question.created_at, \nSUM(case when vote = 1 then 1 when vote = -1 then -1 else 0 end) as votes_sum, \ncount(question_vote.id) as votes_count \nfrom question \nleft join question_vote \non question.id = question_vote.question_id \ngroup by question.id \n;\n\nCREATE VIEW answer_view AS \nselect answer.id, answer.user_id, question_id, text, answer.created_at, \nSUM(case when vote = 1 then 1 when vote = -1 then -1 else 0 end) as votes_sum, \ncount(answer_vote.id) as votes_count \nfrom answer \nleft join answer_vote \non answer.id = answer_vote.answer_id \ngroup by answer.id \n;";		Tools.writeRQL(s);


		Tools.dbInit();
		ProductView pv = ProductView.findFirst(
				"id = ?", 1);



		String json = Tools.nodeToJson(
				Transformations.productViewJson(pv));

		Tools.dbClose();

		System.out.println(json);
	}




}
