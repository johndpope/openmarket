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



		String s2="DROP VIEW category_children;";
		Tools.writeRQL(s2);




		String s="CREATE VIEW category_children AS \nWITH RECURSIVE t(n) AS (VALUES(3) UNION select id from category, t where parent=t.n) SELECT * from t;";
//		Tools.writeRQL(s);
		
		

	}




}
