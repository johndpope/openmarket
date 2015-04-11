package openmarket;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.openmarket.db.Tables.*;

import com.openmarket.db.Transformations;
import com.openmarket.db.actions.Actions.CategoryActions;
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
//		log.info(CategoryActions.getCategoryThumbnails("1"));
//		log.info(CATEGORY_CHILDREN.find("id = ?", 1).toJson(true));
		
//		log.info(Tools.nodeToJsonPretty(Tools.rqlStatus()));
		Tools.runCommand(DataSources.KEYTOOL_CMD);
		Tools.dbClose();
		
	}
	
	
	

	

}
