package com.openmarket.webservice;

import static spark.Spark.post;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openmarket.db.Tables.User;
import com.openmarket.db.actions.Actions.UserActions;
import com.openmarket.tools.Tools;

public class Platform {
	
	static final Logger log = LoggerFactory.getLogger(Platform.class);


	public static void setup() {


		post("/send_signup_email", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				String email = Tools.createMapFromAjaxPost(req.body()).get("email");
				
				Tools.dbInit();
				User user = User.create("email", email);
				Tools.writeRQL(user.toInsert());
				user = User.findFirst("email = ?", email);
				
				String message = UserActions.sendSignUpEmail(user);
				
				Tools.dbClose();
				
				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			}

		});
	}
}
