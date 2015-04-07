package com.openmarket.tools;


public class DataSources {

	public static String APP_NAME = "openmarket";



	public static Integer SPARK_WEB_PORT = 4567;

	public static String URL() {
		return "http://localhost:" + SPARK_WEB_PORT + "/";
	}




	public static Boolean IS_SSL = false;

	public static String HTTP = (IS_SSL) ? "https://" : "http://";

	public static String EXTERNAL_IP = Tools.httpGet("http://checkip.amazonaws.com/").trim();


	public static String WEB_SERVICE_EXTERNAL_URL(){
		return  HTTP + EXTERNAL_IP + ":" + SPARK_WEB_PORT + "/";
	}

	public static String WEB_SERVICE_INTERNAL_URL() {
		return HTTP + "localhost:" + SPARK_WEB_PORT + "/";
	}

	public static String SET_PASSWORD_URL() {
		return URL() + "set_password";
	}

	// The path to the openmarket dir
	public static String HOME_DIR = System.getProperty( "user.home" ) + "/." + APP_NAME;

	// This should not be used, other than for unzipping to the home dir
	public static final String CODE_DIR = System.getProperty("user.dir");

	public static final String SOURCE_CODE_HOME() {return HOME_DIR + "/src";}

	public static final String SHADED_JAR_FILE = CODE_DIR + "/target/" + APP_NAME + ".jar";

	public static final String SHADED_JAR_FILE_2 = CODE_DIR + "/" + APP_NAME + ".jar";

	public static final String ZIP_FILE() {return HOME_DIR + "/" + APP_NAME + ".zip";}

	public static final String TOOLS_JS() {return SOURCE_CODE_HOME() + "/web/js/tools.js";}

	public static final String DB_FILE() {return HOME_DIR + "/db/data/db.sqlite";}

	public static final String SQL_FILE() {return SOURCE_CODE_HOME() + "/ddl.sql";}

	public static final String SQL_VIEWS_FILE() {return SOURCE_CODE_HOME() + "/views.sql";}

	public static final String BUTTON_JSON_REQ ="{\n \"button\" : {\n    \"name\": \"kittin mittinz\",\n    \"type\": \"buy_now\",\n       \"text\": \"Buy with USD/BTC\",\n    \"price_string\": \"0.50\",\n    \"price_currency_iso\": \"USD\",\n  \"network\": \"test\",\n   \"callback_url\": \"http://www.example.com/my_custom_button_callback\",\n    \"description\": \"Sample description\"\n  }\n   \n}";

	public static final String KEYSTORE_FILE() {return HOME_DIR + "/keystore.jks";}

	public static final String KEYSTORE_PASSWORD_FILE() {return HOME_DIR + "/pass";}

	public static final String KEYSTORE_DOMAIN_FILE() {return HOME_DIR + "/domain";}

	// RQL 
	public static String RQL_DIR() {return HOME_DIR + "/db";}

	public static String MASTER_NODE_IP = "localhost";

	public static String MASTER_NODE_PORT = "4001";

	public static final String MASTER_NODE_URL = "http://" + MASTER_NODE_IP + ":" + MASTER_NODE_PORT;

	public static final Boolean IS_MASTER_NODE = MASTER_NODE_IP.equals("localhost") || 
			MASTER_NODE_IP.equals(EXTERNAL_IP);

	public static final Integer RQL_PORT = 4001;

	public static final String RQLITE_INSTALL_SCRIPT() {return SOURCE_CODE_HOME() + "/rql_setup.sh";}

	public static final String RQLITE_JOIN_SCRIPT() {return SOURCE_CODE_HOME() + "/rql_join.sh";}

	public static final String RQLITE_STARTUP_SCRIPT() {return SOURCE_CODE_HOME() + "/rql_startup.sh";}


	// Properties files
	public static final String EMAIL_PROP = SOURCE_CODE_HOME() + "/email.properties";

	public static final String SIGNUP_EMAIL_TEMPLATE = SOURCE_CODE_HOME() + "/signup_email_template.html";

	// Google categories
	public static final String GOOGLE_CATEGORIES_LIST = SOURCE_CODE_HOME() + "/categories.list";

	// Countries
	public static final String COUNTRIES_LIST = SOURCE_CODE_HOME() + "/countries.list";

	// Web pages
	public static final String WEB_HOME() {return SOURCE_CODE_HOME() + "/web";}

	public static final String WEB_HTML() {return WEB_HOME() + "/html";}


	public static final String PAGES(String pageName) {
		return WEB_HTML() + "/" + pageName + ".html";
	}
	
	
}
