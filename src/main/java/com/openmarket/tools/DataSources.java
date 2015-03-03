package com.openmarket.tools;


public class DataSources {
	
public static String APP_NAME = "openmarket";
	
	
	
	public static final Integer SPARK_WEB_PORT = 4567;
	
	
	public static final String EXTERNAL_IP = Tools.httpGet("http://checkip.amazonaws.com/").trim();
	
	public static String WEB_SERVICE_URL = "http://" + EXTERNAL_IP + ":" + SPARK_WEB_PORT + "/";
	
	// The path to the openmarket dir
	public static String HOME_DIR = System.getProperty( "user.home" ) + "/." + APP_NAME;
	
	// This should not be used, other than for unzipping to the home dir
	public static final String CODE_DIR = System.getProperty("user.dir");
	
	public static final String SOURCE_CODE_HOME = HOME_DIR + "/src";
	
	
	public static final String SHADED_JAR_FILE = CODE_DIR + "/target/" + APP_NAME + "-shaded.jar";
	
	public static final String SHADED_JAR_FILE_2 = CODE_DIR + "/" + APP_NAME + "-shaded.jar";
	
	public static final String ZIP_FILE = HOME_DIR + "/" + APP_NAME + "-shaded.zip";
	
	public static final String TOOLS_JS = SOURCE_CODE_HOME + "/web/js/tools.js";
	
	public static final String DB_FILE = HOME_DIR + "/db/data/db.sqlite";

	public static final String SQL_FILE = SOURCE_CODE_HOME + "/ddl.sql";
	
	public static final String SQL_VIEWS_FILE = SOURCE_CODE_HOME + "/views.sql";
	
	public static final String BUTTON_JSON_REQ ="{\n \"button\" : {\n    \"name\": \"kittin mittinz\",\n    \"type\": \"buy_now\",\n       \"text\": \"Buy with USD/BTC\",\n    \"price_string\": \"0.50\",\n    \"price_currency_iso\": \"USD\",\n  \"network\": \"test\",\n   \"callback_url\": \"http://www.example.com/my_custom_button_callback\",\n    \"description\": \"Sample description\"\n  }\n   \n}";
	
	public static final String KEYSTORE_FILE = HOME_DIR + "/keystore.jks";
	
	public static final String KEYSTORE_PASSWORD_FILE = HOME_DIR + "/pass";
	
	public static final String KEYSTORE_DOMAIN_FILE = HOME_DIR + "/domain";
	
	// RQL 
	public static final String RQL_DIR = HOME_DIR + "/db";
	
	public static final String MASTER_NODE_URL = "http://localhost:4001";
	
	public static final Integer RQL_PORT = 4001;
	
	public static final String RQL_SETUP_SCRIPT = SOURCE_CODE_HOME + "/rql_setup.sh";
	
	public static final String RQL_REJOIN_SCRIPT = SOURCE_CODE_HOME + "/rql_rejoin.sh";
	
	
}
