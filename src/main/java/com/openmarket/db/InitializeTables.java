package com.openmarket.db;

import static com.openmarket.db.Tables.Currency;
import static com.openmarket.db.Tables.TimeSpan;
import static com.openmarket.db.Tables.TimeType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openmarket.tools.DataSources;
import com.openmarket.tools.TableConstants;
import com.openmarket.tools.Tools;

public class InitializeTables {


	static final Logger log = LoggerFactory.getLogger(InitializeTables.class);



	public static void init(Boolean delete) {

		log.info("Using database located at : " + DataSources.DB_FILE());


		if (delete) {
			deleteDB();
			//			fillTables();
		} 

		setupOrRejoinRQL();




		// For some reason, this needs to wait 5 seconds for the write locks to release


	}

	public static void setupOrRejoinRQL() {

		try {

			Boolean rqlDirExists = new File(DataSources.RQL_DIR()).exists();

			Boolean firstRqlRun = false;

			log.info("rql dir = " + DataSources.RQL_DIR());
			if (!rqlDirExists) {

				log.info("Installing rqlite...(done only once to connect to the network)");


				java.nio.file.Files.write(Paths.get(DataSources.RQLITE_INSTALL_SCRIPT()),
						TableConstants.INSTALL_RQLITE_SCRIPT_LINES());

				Tools.runScript(DataSources.RQLITE_INSTALL_SCRIPT());

				// For some reason the joining command is a weird one, then after initializing its 
				// like regular.
				if (!DataSources.IS_MASTER_NODE) {
					log.info("Joining rqlite master node @ " + DataSources.MASTER_NODE_URL);
					java.nio.file.Files.write(Paths.get(DataSources.RQLITE_JOIN_SCRIPT()),
							TableConstants.RQLITE_JOIN_LINES());

					Tools.runScript(DataSources.RQLITE_JOIN_SCRIPT());
				} 

				firstRqlRun = true;



			}

			log.info("Starting rqlite...");
			java.nio.file.Files.write(Paths.get(DataSources.RQLITE_STARTUP_SCRIPT()), 
					TableConstants.RQLITE_STARTUP_SCRIPT_LINES());

			RQLite.start();

			// Let it start up
			Tools.sleep(5000);

			// This means you are the master node, so you need to set up the tables from scratch
			if (DataSources.IS_MASTER_NODE && firstRqlRun) {
				log.info("Filling the tables, be aware that this will take ~20 minutes due to filling"
						+ " over 16000 product categories from google's product taxonomy.");
				createTables();
				fillTables();
			}




		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void deleteDB() {

		try {
			FileUtils.deleteDirectory(new File(DataSources.RQL_DIR()));
			log.info("DB deleted");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void createTables() {

		try {

			if (!new File(DataSources.DB_FILE()).exists()) {
				log.info("Creating tables/running the DDL...");
				Tools.runRQLFile(new File(DataSources.SQL_FILE()));
				Tools.runRQLFile(new File(DataSources.SQL_VIEWS_FILE()));

				log.info("Tables created successfully");
			} else {
				log.info("DB already exists");

			}
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	public static void fillTables() {
		log.info("Filling tables...");

		setupCategories();
		//		Tools.sleep(5000);
		setupProcessingTimeSpans();
		setupCurrencies();

		setupCountries();

	}

	public static void setupCountries() {
		List<Entry<String, String>> list = Tools.readCountries().subList(0, 25);
		String s = Tools.countriesToInserts(list);

		Tools.writeRQL(s);
	}

	public static void setupCategories() {
		List<Entry<String, Integer>> list = Tools.readGoogleProductCategories().subList(0, 25);
		String s = Tools.googleProductCategoriesToInserts(list);

		Tools.writeRQL(s);
	}

	public static void setupCurrencies() {

		StringBuilder s = new StringBuilder();
		Tools.dbInit();
		for (Entry<String, String> e : TableConstants.CURRENCY_MAP.entrySet()) {
			// Unicode still not working

			String cmd = Currency.create("iso", e.getKey(), "desc", e.getValue(), 
					"unicode" , TableConstants.CURRENCY_UNICODES.get(e.getKey())).toInsert();
			s.append(";\n");
			s.append(cmd);
		}
		Tools.dbClose();

		Tools.writeRQL(s.toString());
	}

	public static void setupProcessingTimeSpans() {
		Tools.dbInit();
		StringBuilder s = new StringBuilder();
		for (String e: TableConstants.TIME_TYPES) {
			String cmd = TimeType.create("name", e).toInsert();
			s.append(cmd);
			s.append(";\n");
		}
		Tools.dbClose();
		Tools.writeRQL(s.toString());

		Tools.dbInit();
		s = new StringBuilder();

		for (TableConstants.ProcessingTime e : TableConstants.PROCESSING_TIME_SPANS) {
			String cmd = TimeSpan.create("time_type_id", e.getTimeTypeId(),
					"min", e.getMin(),
					"max", e.getMax()).toInsert();
			s.append(cmd);
			s.append(";\n");
		}
		Tools.dbClose();
		Tools.writeRQL(s.toString());

	}



	//	public static void fillTables() {
	//		Tools.dbInit();
	//
	//		log.info("Filling tables...");
	//
	//		setupCurrencies();
	//		setupButtonStyles();
	//		setupButtonTypes();
	//		setupOrderStatuses();
	//		setupMerchantInfo();
	//
	//		Tools.dbClose();
	//		log.info("Filled Tables succesfully");
	//	}
	//
	//
	//
	//	private static void setupCurrencies() {
	//
	//		for (Entry<String, String> e : TableConstants.CURRENCY_MAP.entrySet()) {
	//			// Unicode still not working
	//			Currency.createIt("iso", e.getKey(), "desc", e.getValue(), "unicode" , TableConstants.CURRENCY_UNICODES.get(e.getKey()));
	//		}
	//	}
	//
	//	private static void setupButtonStyles() {
	//		for (String style: TableConstants.BUTTON_STYLES) {
	//			ButtonStyle.createIt("style", style);
	//		}
	//	}
	//
	//	private static void setupOrderStatuses() {
	//		for (String status : TableConstants.ORDER_STATUSES) {
	//			OrderStatus.createIt("status", status);
	//		}
	//	}
	//
	//	private static void setupButtonTypes() {
	//		for (String type: TableConstants.BUTTON_TYPES) {
	//			ButtonType.createIt("type", type);
	//		}
	//	}
	//
	//	private static void setupMerchantInfo() {
	//		Integer currencyId = TableConstants.CURRENCY_LIST().indexOf("USD") + 1;
	//		MerchantInfo.createIt("currency_id", currencyId);
	//
	//	}


}
