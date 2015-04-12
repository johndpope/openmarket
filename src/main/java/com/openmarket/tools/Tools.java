package com.openmarket.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.DBException;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.bitmerchant.tools.CurrencyConverter;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.openmarket.db.InitializeTables;

public class Tools {

	static final Logger log = LoggerFactory.getLogger(Tools.class);

	public static final Gson GSON = new Gson();
	public static final Gson GSON2 = new GsonBuilder().setPrettyPrinting().create();

	public static final ObjectMapper MAPPER = new ObjectMapper();

	public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").
			withZone(DateTimeZone.UTC);
	public static final DateTimeFormatter DTF2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").
			withZone(DateTimeZone.UTC);
	public static final DateTimeFormatter DTF3 = DateTimeFormat.forPattern("yyyy-MM-dd").
			withZone(DateTimeZone.UTC);

	public static final StrongPasswordEncryptor PASS_ENCRYPT = new StrongPasswordEncryptor();

	// Instead of using session ids, use a java secure random ID
	private static final SecureRandom RANDOM = new SecureRandom();

	public static void addExternalWebServiceVarToTools() {

		log.info("tools.js = " + DataSources.TOOLS_JS());
		try {
			List<String> lines = java.nio.file.Files.readAllLines(Paths.get(DataSources.TOOLS_JS()));

			String interalServiceLine = "var localSparkService = '" + 
					DataSources.WEB_SERVICE_INTERNAL_URL() + "';";

			String externalServiceLine = "var externalSparkService ='" + 
					DataSources.WEB_SERVICE_EXTERNAL_URL() + "';";

			lines.set(0, interalServiceLine);
			lines.set(1, externalServiceLine);

			java.nio.file.Files.write(Paths.get(DataSources.TOOLS_JS()), lines);
			Files.touch(new File(DataSources.TOOLS_JS()));


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String toUpdate(String tableName, String id, Object... namesAndValues) {


		StringBuilder s = new StringBuilder();

		s.append("UPDATE " + tableName + " SET ");

		for (int i = 0; i < namesAndValues.length - 1; i+=2) {
			Object field = namesAndValues[i];
			Object value = namesAndValues[i+1];
			if (value != null) {
				s.append(field + " = " + "'" + value + "'");
			} else {
				s.append(field + " = null" );
			}

			if (i+2 < namesAndValues.length) {
				s.append(" , ");
			}
		}

		s.append(" WHERE id = " + id + ";");


		return s.toString();

	}

	public static String toUpdate(String tableName, Set<String> ids, Object... namesAndValues) {


		StringBuilder s = new StringBuilder();

		s.append("UPDATE " + tableName + " SET ");

		for (int i = 0; i < namesAndValues.length - 1; i+=2) {
			Object field = namesAndValues[i];
			Object value = namesAndValues[i+1];
			if (value != null) {
				s.append(field + " = " + "'" + value + "'");
			} else {
				s.append(field + " = null" );
			}

			if (i+2 < namesAndValues.length) {
				s.append(" , ");
			}
		}

		s.append(" WHERE ");
		Iterator<String> idIt = ids.iterator();
		for (;;) {
			String id = idIt.next();
			s.append(" id = " + id);

			if (idIt.hasNext()) {
				s.append(" OR ");
			} else {
				break;
			}
		}
		s.append(";");

		return s.toString();

	}

	public static String toUpdate(String tableName, String conditionCol, Integer condition, Object... namesAndValues) {


		StringBuilder s = new StringBuilder();

		s.append("UPDATE " + tableName + " SET ");

		for (int i = 0; i < namesAndValues.length - 1; i+=2) {
			Object field = namesAndValues[i];
			Object value = namesAndValues[i+1];
			if (value != null) {
				s.append(field + " = " + "'" + value + "'");
			} else {
				s.append(field + " = null" );
			}

			if (i+2 < namesAndValues.length) {
				s.append(" , ");
			}
		}

		s.append(" WHERE " + conditionCol + " = " + condition + ";");


		return s.toString();

	}

	public static String toDelete(String tableName, String id) {


		StringBuilder s = new StringBuilder();


		s.append("DELETE FROM " + tableName + " ");
		s.append("WHERE id = " + id + ";");


		return s.toString();

	}

	public static String parseMustache(Map<String, Object> vars, String templateFile) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//		String templateString;
		try {
			//			templateString = new String(java.nio.file.Files.readAllBytes(Paths.get(templateFile)));


			Writer writer = new OutputStreamWriter(baos);
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile(new FileReader(templateFile), "example");
			mustache.execute(writer, vars);


			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String output = baos.toString();
		log.info(output);



		return output;






	}

	public static Properties loadProperties(String propertiesFileLocation) {

		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(propertiesFileLocation);

			// load a properties file
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;

	}


	public static String sendEmail(String toEmail, String replyToEmail, String subject, String text) {

		Properties props = Tools.loadProperties(DataSources.EMAIL_PROP());
		final String username = props.getProperty("username");
		log.info("user-email-name = " + username);
		final String password =  props.getProperty("password");

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("Noreply_bitpieces@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toEmail));
			message.setSubject(subject);

			if (replyToEmail != null) {
				message.setReplyTo(new javax.mail.Address[] {
						new InternetAddress(replyToEmail)
				});
			}

			//			message.setText(text);
			message.setContent(text, "text/html");


			Transport.send(message);

			log.info("Done");

		} catch (MessagingException e) {
			throw new NoSuchElementException(e.getMessage());
		}

		String message = "Email sent to " + toEmail;

		return message;

	}

	public static String generateSecureRandom() {
		return new BigInteger(256, RANDOM).toString(32);
	}

	public static void allowOnlyLocalHeaders(Request req, Response res) {


		log.info("req ip = " + req.ip());


		//		res.header("Access-Control-Allow-Origin", "http://mozilla.com");
		//		res.header("Access-Control-Allow-Origin", "null");
		//		res.header("Access-Control-Allow-Origin", "*");
		//		res.header("Access-Control-Allow-Credentials", "true");


		if (!isLocalIP(req.ip())) {
			throw new NoSuchElementException("Not a local ip, can't access");
		}
	}

	public static Boolean isLocalIP(String ip) {
		Boolean isLocalIP = (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1"));
		return isLocalIP;
	}

	public static void allowAllHeaders(Request req, Response res) {
		String origin = req.headers("Origin");
		res.header("Access-Control-Allow-Credentials", "true");
		res.header("Access-Control-Allow-Origin", origin);


	}



	public static void logRequestInfo(Request req) {
		String origin = req.headers("Origin");
		String origin2 = req.headers("origin");
		String host = req.headers("Host");


		log.debug("request host: " + host);
		log.debug("request origin: " + origin);
		log.debug("request origin2: " + origin2);


		//		System.out.println("origin = " + origin);
		//		if (DataSources.ALLOW_ACCESS_ADDRESSES.contains(req.headers("Origin"))) {
		//			res.header("Access-Control-Allow-Origin", origin);
		//		}
		for (String header : req.headers()) {
			log.debug("request header | " + header + " : " + req.headers(header));
		}
		log.debug("request ip = " + req.ip());
		log.debug("request pathInfo = " + req.pathInfo());
		log.debug("request host = " + req.host());
		log.debug("request url = " + req.url());
	}

	public static final Map<String, String> createMapFromAjaxPost(String reqBody) {
		log.debug(reqBody);
		Map<String, String> postMap = new HashMap<String, String>();
		String[] split = reqBody.split("&");
		for (int i = 0; i < split.length; i++) {
			String[] keyValue = split[i].split("=");
			try {
				if (keyValue.length > 1) {
					postMap.put(URLDecoder.decode(keyValue[0], "UTF-8"),URLDecoder.decode(keyValue[1], "UTF-8"));
				}
			} catch (UnsupportedEncodingException |ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				throw new NoSuchElementException(e.getMessage());
			}
		}

		log.debug(GSON2.toJson(postMap));

		return postMap;

	}

	public static void runScript(String path) {
		try {
			File file = new File(path);
			file.setExecutable(true);
			ProcessBuilder pb = new ProcessBuilder(
					path).inheritIO();
			Process p = pb.start();     // Start the process.
			log.info("Executing script " + path);
			p.waitFor();                // Wait for the process to finish.
			log.info("Script executed successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void runCommand(String cmd) {
		try {

			ProcessBuilder pb = new ProcessBuilder("bash", "-c",
					cmd).inheritIO();
			Process p = pb.start();     // Start the process.
			p.waitFor();                // Wait for the process to finish.

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static final void dbInit() {
		try {
			new DB("default").open("org.sqlite.JDBC", "jdbc:sqlite:" + DataSources.DB_FILE(), "root", "p@ssw0rd");
		} catch (DBException e) {
			e.printStackTrace();
			dbClose();
			dbInit();
		}

	}

	public static final void dbClose() {
		new DB("default").close();
	}

	public static final String httpGet(String url) {
		String res = "";
		try {
			URL externalURL = new URL(url);

			URLConnection yc = externalURL.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) 
				res+="\n" + inputLine;
			in.close();

			return res;
		} catch(IOException e) {}
		return res;
	}

	public static void runSQLFile(Connection c,File sqlFile) {

		try {
			Statement stmt = null;
			stmt = c.createStatement();
			String sql;

			sql = Files.toString(sqlFile, Charset.defaultCharset());

			stmt.executeUpdate(sql);
			stmt.close();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void runRQLFile(File sqlFile) {
		try {
			String sql;

			sql = Files.toString(sqlFile, Charset.defaultCharset());

			writeRQL(sql);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String reformatSQLForRQL(String sql) {

		String reformat = sql.replace("\n", "").replace("\r", "").replace("'", "\"")
				.replace(");",");\n").replace("\n;", ";\n");


		return reformat;
	}

	public static String writeRQL(String cmd) {

		String reformatted = reformatSQLForRQL(cmd);

		String postURL = DataSources.RQL_MY_NODE_URL() + "/db?pretty";
		
		log.info("rql write string : " + reformatted + "\npostUrl: " + postURL);

		String message = "";
		try {

			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpPost httpPost = new HttpPost(postURL);
			httpPost.setEntity(new StringEntity(reformatted));
//			httpPost.setEntity(new StringEntity("L"));

			ResponseHandler<String> handler = new BasicResponseHandler();

			CloseableHttpResponse response = httpClient.execute(httpPost);

			message = handler.handleResponse(response);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		message = "Rqlite write status : " + message;
		log.info(message);
		return message;
	}

	public static ObjectNode rqlStatus() {

		ObjectNode on = MAPPER.createObjectNode();



		on.put("raft", jsonToNode(rqlGetEndpoint("raft")));
		on.put("diagnostics", jsonToNode(rqlGetEndpoint("diagnostics")));
		on.put("statistics", jsonToNode(rqlGetEndpoint("statistics")));


		return on;
	}

	public static String rqlGetEndpoint(String endPoint) {
		String postURL = DataSources.RQL_MY_NODE_URL() + "/" + endPoint + "?pretty";

		String message = "";
		try {

			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpGet httpPost = new HttpGet(postURL);
			//			httpPost.setEntity(new StringEntity(reformatted));

			ResponseHandler<String> handler = new BasicResponseHandler();

			CloseableHttpResponse response = httpClient.execute(httpPost);

			message = handler.handleResponse(response);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return message;
	}

	public static JsonNode jsonToNode(String json) {

		ObjectMapper mapper = new ObjectMapper();

		try {

			JsonNode root = mapper.readTree(json);

			return root;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void initializeDBAndSetupDirectories(Boolean delete) {

		if (delete) {
			try {
				FileUtils.deleteDirectory(new File(DataSources.HOME_DIR()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		setupDirectories();

		copyResourcesToHomeDir(true);




		// Initialize the DB if it hasn't already
		InitializeTables.init(delete);


	}

	public static void initializeSSL() {



		// if the keystore file doesn't exist at that directory, then generate it:
		if (!new File(DataSources.KEYSTORE_FILE()).exists()) {

			// Delete the old file if it exists or you're running this from a different dir
			if (new File("keystore.jks").exists()) {
				try {
					java.nio.file.Files.delete(Paths.get("keystore.jks"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			Tools.runCommand(DataSources.KEYTOOL_CMD);

			// copy that file to the home dir
			try {
				// delete the old one if its there

				Files.copy(new File("keystore.jks"), new File(DataSources.KEYSTORE_FILE()));
				DataSources.IS_SSL = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			DataSources.IS_SSL = true;
		}

	}


	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void setupDirectories() {
		if (!new File(DataSources.HOME_DIR()).exists()) {
			log.info("Setting up ~/." + DataSources.APP_NAME + " dirs");
			new File(DataSources.HOME_DIR()).mkdirs();
		} else {
			log.info("Home directory already exists");
		}
	}

	public static void copyResourcesToHomeDir(Boolean copyAnyway) {


		String zipFile = null;

		if (copyAnyway || !new File(DataSources.SOURCE_CODE_HOME()).exists()) {
			log.info("Copying resources to  ~/." + DataSources.APP_NAME + " dirs");

			try {
				if (new File(DataSources.SHADED_JAR_FILE).exists()) {
					java.nio.file.Files.copy(Paths.get(DataSources.SHADED_JAR_FILE), Paths.get(DataSources.ZIP_FILE()), 
							StandardCopyOption.REPLACE_EXISTING);
					zipFile = DataSources.SHADED_JAR_FILE;

				} else if (new File(DataSources.SHADED_JAR_FILE_2).exists()) {
					java.nio.file.Files.copy(Paths.get(DataSources.SHADED_JAR_FILE_2), Paths.get(DataSources.ZIP_FILE()),
							StandardCopyOption.REPLACE_EXISTING);
					zipFile = DataSources.SHADED_JAR_FILE_2;
				} else {
					log.info("you need to build the project first");
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			Tools.unzip(new File(zipFile), new File(DataSources.SOURCE_CODE_HOME()));
			//		new Tools().copyJarResourcesRecursively("src", configHome);
		} else {
			log.info("The source directory already exists");
		}
	}

	public static void unzip(File zipfile, File directory) {
		try {
			ZipFile zfile = new ZipFile(zipfile);
			Enumeration<? extends ZipEntry> entries = zfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File file = new File(directory, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					file.getParentFile().mkdirs();
					InputStream in = zfile.getInputStream(entry);
					try {
						copy(in, file);
					} finally {
						in.close();
					}
				}
			}

			zfile.close();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readFile(String path) {
		String s = null;

		byte[] encoded;
		try {
			encoded = java.nio.file.Files.readAllBytes(Paths.get(path));

			s = new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

	private static void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);
		} finally {
			out.close();
		}
	}
	public static List<Map.Entry<String,String>> readCountries() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";


		// The resulting list
		List<Map.Entry<String,String>> countryList = new ArrayList<Map.Entry<String,String>>();
		try {

			br = new BufferedReader(new FileReader(DataSources.COUNTRIES_LIST));

			while ((line = br.readLine()) != null) {

				String[] vars = line.split(cvsSplitBy);

				String name = WordUtils.capitalizeFully(vars[0].toLowerCase().replace("'", ""));
				String code = vars[1];

				Map.Entry<String,String> nameAndCode = 
						new java.util.AbstractMap.SimpleEntry<>(name, code);


				// Add to the adjency list
				countryList.add(nameAndCode);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		return countryList;
	}

	public static String countriesToInserts(
			List<Map.Entry<String,String>> countryList) {

		StringBuilder s = new StringBuilder();

		for (Entry<String, String> e : countryList) {
			s.append("INSERT INTO country (name,country_code) "
					+ "VALUES ('" + e.getKey() + "','" + e.getValue() + "');");
			s.append("\n");
		}

		return s.toString();

	}

	public static List<Map.Entry<String,Integer>> readGoogleProductCategories() {


		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ">";


		// Need a map from the name, to its index
		Map<String, Integer> nameToIndexMap = new HashMap<>();

		// The resulting list
		List<Map.Entry<String,Integer>> adjacencyList = new ArrayList<Map.Entry<String,Integer>>();

		try {

			br = new BufferedReader(new FileReader(DataSources.GOOGLE_CATEGORIES_LIST));
			Integer i = 0;
			while ((line = br.readLine()) != null) {
				i++;
				String[] vars = line.split(cvsSplitBy);

				String lastCategory = vars[vars.length-1].trim();
				nameToIndexMap.put(lastCategory, i);

				Integer parentIndex = null;
				if (vars.length > 1) {
					// Now get the 2nd to last one, that is the parent
					String parentName = vars[vars.length-2].trim();

					// get that parents index
					parentIndex = nameToIndexMap.get(parentName);


				} 

				Map.Entry<String,Integer> nameAndParentIndex = 
						new java.util.AbstractMap.SimpleEntry<>(lastCategory,parentIndex);

				// Add to the adjency list
				adjacencyList.add(nameAndParentIndex);



				//				System.out.println(nameAndParentIndex);



			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		return adjacencyList;
	}

	public static String googleProductCategoriesToInserts(
			List<Map.Entry<String,Integer>> adjacencyList) {

		StringBuilder s = new StringBuilder();

		for (Entry<String, Integer> e : adjacencyList) {
			s.append("INSERT INTO category (name,parent) "
					+ "VALUES ('" + e.getKey() + "'," + e.getValue() + ");");
			s.append("\n");
		}

		return s.toString();

	}

	public static String nodeToJson(ObjectNode a) {
		try {
			return Tools.MAPPER.writeValueAsString(a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String nodeToJsonPretty(ObjectNode a) {
		try {
			return Tools.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {
 "button" : {
    "name": "kitten mittens",
    "type": "buy_now",
    "style": "custom_large",
    "network": "test",
    "text": "Buy with USD",
    "price_string": "5",
    "price_currency_iso": "USD",
    "callback_url": "http://www.example.com/my_custom_button_callback",
    "description": "Is your cat making too much noise all the time?"
  }
}

	 */

	public static String createBitmerchantButtonRequest(String total,
			String iso, String paymentId) {

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ObjectNode b = Tools.MAPPER.createObjectNode();

		b.put("name", "Order #" + paymentId);
		b.put("type", "buy_now");
		b.put("style", "custom_large");
		b.put("text", "Buy with derp");
		b.put("price_string", total);
		b.put("price_currency_iso", iso);
		b.put("callback_url", DataSources.WEB_SERVICE_EXTERNAL_URL() + "callback/" + paymentId);
		b.put("description", "Order #" + paymentId);
		a.put("button", b);

		return Tools.nodeToJson(a);

	}

	public static String createBitmerchantIframe(String buttonId, String ip) {
		String s="<iframe id=\"bitmerchant_iframe\" name=\"" + buttonId + "\" src=\"" + ip + "html/payment_iframe.html\" "
				+ " "
				+ "style=\"width: 460px; height: 300px; border: none; "
				+ "\" allowtransparency=\"true\" frameborder=\"0\" white-space=\"nowrap\"></iframe>\n"+
				"";

		return s.replace("\"", "doublequote").replace(";","semicolon");
	}

	public static void cacheCurrency(String currency) {
		// Start up the currency converter just to pre cache it
		try {
			CurrencyConverter.INSTANCE.getBtcRatesCache().get(CurrencyUnit.of(currency));
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JsonNode sendBitmerchantRequestToSeller(String jsonReq, String sellerIP) {

		log.info("json req : " + jsonReq);
		

		String postURL = sellerIP + "api/create_order";
		
		log.info("post url : " + postURL);

		String message = "";
		try {

			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpPost httpPost = new HttpPost(postURL);
			httpPost.setEntity(new StringEntity(jsonReq));

			ResponseHandler<String> handler = new BasicResponseHandler();

			CloseableHttpResponse response = httpClient.execute(httpPost);

			message = handler.handleResponse(response);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(message);
		
		JsonNode on = jsonToNode(message);
		return on;
	}
	
	
	


}
