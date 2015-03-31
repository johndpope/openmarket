package com.openmarket.webservice;

import static spark.Spark.get;
import static spark.SparkBase.externalStaticFileLocation;
import static spark.SparkBase.setPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitmerchant.webservice.API;
import com.bitmerchant.webservice.WalletService;
import com.openmarket.tools.DataSources;
import com.openmarket.tools.Tools;


public class WebService {

	static final Logger log = LoggerFactory.getLogger(WebService.class);


	public static void start() {

		//		setupSSL();

		// Add external web service url to beginning of javascript tools
		//		Tools.addExternalWebServiceVarToTools();

		setPort(DataSources.SPARK_WEB_PORT) ;



		//		staticFileLocation("/web"); // Static files
		//		staticFileLocation("/web/html"); // Static files
		externalStaticFileLocation(DataSources.WEB_HOME());

		// Set up the secure keystore


		//		WalletService.setup();
		//		API.setup();
		
		get("/hello", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);
			return "hi from the openmarket wallet web service";
		});

		
		Platform.setup();
		
		WalletService.setup();
		API.setup();
		
	
		// All the simple webpages
		get("/:page", (req, res) -> {
			Tools.allowOnlyLocalHeaders(req, res);	
			String pageName = req.params(":page");
			return Tools.readFile(DataSources.PAGES(pageName));
		});
	

		




	}

	//	public static void setupSSL() {
	//
	//		try {
	//			if (new File(DataSources.KEYSTORE_FILE).exists() && 
	//					new File(DataSources.KEYSTORE_PASSWORD_FILE).exists() && 
	//					new File(DataSources.KEYSTORE_DOMAIN_FILE).exists()) {
	//				
	//				String pass = new String(Files.readAllBytes(Paths.get(DataSources.KEYSTORE_PASSWORD_FILE))).trim();
	//				String domain = new String(Files.readAllBytes(Paths.get(DataSources.KEYSTORE_DOMAIN_FILE))).trim();
	//	
	//				log.info("keystore file = " + DataSources.KEYSTORE_FILE);
	//				SparkBase.setSecure(DataSources.KEYSTORE_FILE, pass,null,null);
	//				LocalWallet.INSTANCE.controller.setIsSSLEncrypted(true);
	//				
	//				// Change the spark web service URL
	//				DataSources.WEB_SERVICE_URL = "https://" + domain + ":" + DataSources.SPARK_WEB_PORT + "/";
	//				
	//				
	//				
	//			} else {
	//				log.info("One of the 3 java keystore files is missing. you need a keystore.jks, domain, and pass file");
	//			}
	//
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//
	//
	//
	//
	//	}

}
