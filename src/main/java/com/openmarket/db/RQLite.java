package com.openmarket.db;

import com.openmarket.tools.DataSources;
import com.openmarket.tools.Tools;

public class RQLite implements Runnable {

	private static Thread t;
	
	private static String rqlScript;
	
	public void run() {
		Tools.runScript(rqlScript);
	}
	
	
	
	public static void start(String script) {
		rqlScript = script;
		RQLite r = new RQLite();
		t = new Thread(r);
		t.start();
		
	}
	
	public static void stop() {
		t.interrupt();
	}

}
