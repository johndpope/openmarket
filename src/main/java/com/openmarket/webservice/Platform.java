package com.openmarket.webservice;

import static spark.Spark.post;



import com.openmarket.db.Tables.User;
import com.openmarket.db.actions.Actions.UserActions;
import com.openmarket.tools.Tools;

public class Platform {


	public static void setup() {


		post("/send_signup_email", (req, res) -> {
			try {
				Tools.allowOnlyLocalHeaders(req, res);
				String email = Tools.createMapFromAjaxPost(req.body()).get("email");
				
				User user = User.create("email", email);
				Tools.writeRQL(user.toInsert());
				
				String message = UserActions.sendSignUpEmail(user);
				
				return message;

			} catch (Exception e) {
				res.status(666);
				return e.getMessage();
			}

		});
	}
}
