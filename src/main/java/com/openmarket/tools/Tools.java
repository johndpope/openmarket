package com.openmarket.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.openmarket.db.InitializeTables;

public class Tools {

	static final Logger log = LoggerFactory.getLogger(Tools.class);

	public static final Gson GSON = new Gson();
	public static final Gson GSON2 = new GsonBuilder().setPrettyPrinting().create();


	public static final DateTimeFormatter DTF2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").
			withZone(DateTimeZone.UTC);
	public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").
			withZone(DateTimeZone.UTC);
	
	public static final StrongPasswordEncryptor PASS_ENCRYPT = new StrongPasswordEncryptor();


	public static void allowOnlyLocalHeaders(Request req, Response res) {


		log.info("req ip = " + req.ip());


		//		res.header("Access-Control-Allow-Origin", "http://mozilla.com");
		//		res.header("Access-Control-Allow-Origin", "null");
		//		res.header("Access-Control-Allow-Origin", "*");
		//		res.header("Access-Control-Allow-Credentials", "true");

		if (!(req.ip().equals("127.0.0.1") || req.ip().equals("0:0:0:0:0:0:0:1"))) {
			throw new NoSuchElementException("Not a local ip, can't access");
		}
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


	public static final void dbInit() {
		try {
			Base.open("org.sqlite.JDBC", "jdbc:sqlite:" + DataSources.DB_FILE, "root", "p@ssw0rd");
		} catch (DBException e) {
			dbClose();
			dbInit();
		}

	}

	public static final void dbClose() {
		Base.close();
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
		
		String reformat = sql.replace("\n", "").replace("\r", "").replace("'", "\"").replace(");", ");\n");;
		
		
		return reformat;
	}

	public static String writeRQL(String cmd) {

		String reformatted = reformatSQLForRQL(cmd);

		String postURL = DataSources.MASTER_NODE_URL + "/db?pretty";

		String message = "";
		try {

			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpPost httpPost = new HttpPost(postURL);
			httpPost.setEntity(new StringEntity(reformatted));

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

	public static void initializeDBAndSetupDirectories(Boolean delete) {

		if (delete) {
			try {
				FileUtils.deleteDirectory(new File(DataSources.HOME_DIR));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		setupDirectories();

		copyResourcesToHomeDir();

		// Initialize the DB if it hasn't already
		InitializeTables.init(delete);

		
	}



	public static void setupDirectories() {
		if (!new File(DataSources.HOME_DIR).exists()) {
			log.info("Setting up ~/." + DataSources.APP_NAME + " dirs");
			new File(DataSources.HOME_DIR).mkdirs();
		} else {
			log.info("Home directory already exists");
		}
	}

	public static void copyResourcesToHomeDir() {


		String zipFile = null;

		if (!new File(DataSources.SOURCE_CODE_HOME).exists()) {
			log.info("Copying resources to  ~/." + DataSources.APP_NAME + " dirs");

			try {
				if (new File(DataSources.SHADED_JAR_FILE).exists()) {
					java.nio.file.Files.copy(Paths.get(DataSources.SHADED_JAR_FILE), Paths.get(DataSources.ZIP_FILE), 
							StandardCopyOption.REPLACE_EXISTING);
					zipFile = DataSources.SHADED_JAR_FILE;

				} else if (new File(DataSources.SHADED_JAR_FILE_2).exists()) {
					java.nio.file.Files.copy(Paths.get(DataSources.SHADED_JAR_FILE_2), Paths.get(DataSources.ZIP_FILE),
							StandardCopyOption.REPLACE_EXISTING);
					zipFile = DataSources.SHADED_JAR_FILE_2;
				} else {
					log.info("you need to build the project first");
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			Tools.unzip(new File(zipFile), new File(DataSources.SOURCE_CODE_HOME));
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

}
