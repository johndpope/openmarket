package com.openmarket.db.actions;

import java.util.Date;
import java.util.Map;

import org.javalite.activejdbc.Model;

import spark.Response;

import com.google.common.collect.ImmutableMap;
import com.openmarket.db.Tables.Login;
import com.openmarket.db.Tables.User;
import com.openmarket.tools.DataSources;
import com.openmarket.tools.TableConstants;
import com.openmarket.tools.Tools;

public class Actions {

	public static class UserActions {

		public static String userLogin(String email, String password, Response res) {

			User user = User.findFirst("email = ?", email);

			if (user == null) {
				res.status(666);
				return "Incorrect email";
			}

			String encryptedPassword = user.getString("password_encrypted");

			Boolean correctPass = Tools.PASS_ENCRYPT.checkPassword(password, encryptedPassword);

			if (correctPass) {
				return WebActions.setCookiesForLogin(user, res);

			} else {
				res.status(666);
				return "Incorrect password";
			}


		}
		
		public static String sendSignUpEmail(User user) {
			
			
			String email = user.getString("email");
					
			String subject = "OpenMarket Sign Up";
			
			String token = Tools.generateSecureRandom();
			
			// Store that token in the user row
			User userUpdate = user.set("email_code", token);
			
			Tools.writeRQL(userUpdate.toInsert());
						
			Map<String, Object> vars = ImmutableMap.<String, Object>builder()
					.put("token", token)
					.build();
			
			String html = Tools.parseMustache(vars, DataSources.SIGNUP_EMAIL_TEMPLATE);
			
			String message = Tools.sendEmail(email, subject, html);
			
			return message;
			
		}
		
		
	}



	public static class WebActions {

		public static String setCookiesForLogin(User user, Response res) {

			Integer expireSeconds = TableConstants.EXPIRE_SECONDS;

			long now = new Date().getTime();

			long expireTime = now + expireSeconds*1000;


			String authenticatedSessionId = Tools.generateSecureRandom();

			// Not sure if this is necessary yet
			Boolean secure = true;

			// Store the users user in the DB, give them a session id
			Login login = Login.create("user_id", user.getId(),
					"session_id", authenticatedSessionId,
					"time_", now,
					"expire_time", expireTime);

			Tools.writeRQL(login.toInsert());


			// Set some cookies for that users login
			res.cookie("authenticated_session_id", authenticatedSessionId, expireSeconds, secure);
			res.cookie("uid", user.getId().toString(), expireSeconds, secure);
			//			String json = Tools.GSON2.toJson(cache);
			//			System.out.println(json);


			return "Logged in";




		}

	}
}
