package openmarket;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.openmarket.db.Tables.User;

import com.openmarket.db.Transformations;
import com.openmarket.tools.DataSources;
import com.openmarket.tools.Tools;

public class TesterDerp extends TestCase {
	static final Logger log = LoggerFactory.getLogger(TesterDerp.class);
	
	public void setUp() {
//		Tools.initializeDBAndSetupDirectories(false);
	}
	
	public void testCreateUser() throws InterruptedException {
			
		DataSources.HOME_DIR = DataSources.HOME_DIR  + "/testnet";
		
		Tools.dbInit();
		System.out.println(Tools.nodeToJsonPretty(Transformations.cartGroupedJson("1")));
		Tools.dbClose();
		
	}
	
	
	

	

}
