package com.openmarket.db;

import com.openmarket.tools.DataSources;
import com.openmarket.tools.Tools;

public class RQLite implements Runnable{

	private static Thread t;
	public void run() {
		Tools.runScript(DataSources.RQL_REJOIN_SCRIPT);
	}
	
	public static void start() {
		RQLite r = new RQLite();
		t = new Thread(r);
		t.start();
		
	}
	
	public static void stop() {
		t.interrupt();
	}

}
