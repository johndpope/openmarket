package com.openmarket.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class TableConstants {

	public static final Integer EXPIRE_SECONDS = 3600;

	public static final Map<String, String> CURRENCY_MAP = ImmutableMap.<String, String>builder()
			.put("BTC", "Bitcoin")
			.put( "USD", "United States Dollar")
			.put( "EUR", "Euro")
			.put( "GBP", "British Pound Sterling")
			.put("AUD","Australian Dollar")
			.put( "BRL", "Brazilian Real")
			.put( "CAD", "Canadian Dollar")
			.put( "CHF", "Swiss Franc")
			.put( "CNY", "Chinese Yuan")


			.put( "HKD", "Hong Kong Dollar")
			.put( "IDR", "Indonesian Rupiah")
			.put( "ILS", "Israeli New Sheqel")
			.put( "MXN", "Mexican Peso")
			.put( "NOK", "Norwegian Krone")
			.put( "NZD", "New Zealand Dollar")
			.put( "PLN", "Polish Zloty")
			.put( "RON", "Romanian Leu")
			.put( "RUB", "Russian Ruble")
			.put( "SEK", "Swedish Krona")
			.put( "SGD", "Singapore Dollar")
			.put( "TRY", "Turkish Lira")

			.put( "ZAR", "South African Rand")
			.build();

	public static final Map<String, String> CURRENCY_UNICODES =  ImmutableMap.<String, String>builder()
			.put("BTC", "\u0E3F")
			.put( "USD", "\u0024")
			.put( "EUR", "\u20AC")
			.put( "GBP", "\u20A4")
			//			.put("mBTC", "m\u0E3F")
			.put("AUD","\u0024")
			.put( "BRL", "R\u0024")
			.put( "CAD", "\u0024")
			.put( "CHF", "\u20A3")
			.put( "CNY", "\u5143")
			.put( "HKD", "\u0024")
			.put( "IDR", "\u20B9")
			.put( "ILS", "\u20AA")
			.put( "MXN", "\u20B1")
			.put( "NOK", "kr")
			.put( "NZD", "\u0024")
			.put( "PLN", "\u007A")
			.put( "RON", "leu")
			.put( "RUB", "\u20BD")
			.put( "SEK", "kr")
			.put( "SGD", "\u0024")
			.put( "TRY", "\u20BA")

			.put( "ZAR", "R")
			.build();


	public static final List<String> CURRENCY_LIST() {
		List<String> currencyList = new ArrayList<>();
		currencyList.addAll(TableConstants.CURRENCY_MAP.keySet());

		return currencyList;

	}

	public static final List<String> TIME_TYPES = Arrays.asList(
			"business days",
			"weeks");

	public static class ProcessingTime {
		private Integer min, max, timeTypeId;

		public ProcessingTime(Integer min, Integer max, Integer timeTypeId) {
			super();
			this.min = min;
			this.max = max;
			this.timeTypeId = timeTypeId;
		}

		public Integer getMin() {
			return min;
		}

		public Integer getMax() {
			return max;
		}

		public Integer getTimeTypeId() {
			return timeTypeId;
		}

	}

	public static final List<ProcessingTime> PROCESSING_TIME_SPANS = Arrays.asList(
			new ProcessingTime(1, 2, TIME_TYPES.indexOf("business days")+1),
			new ProcessingTime(1, 3, TIME_TYPES.indexOf("business days")+1),
			new ProcessingTime(3, 5, TIME_TYPES.indexOf("business days")+1),
			new ProcessingTime(1, 2, TIME_TYPES.indexOf("weeks")+1),
			new ProcessingTime(2, 3, TIME_TYPES.indexOf("weeks")+1),
			new ProcessingTime(3, 4, TIME_TYPES.indexOf("weeks")+1),
			new ProcessingTime(4, 6, TIME_TYPES.indexOf("weeks")+1),
			new ProcessingTime(6, 8, TIME_TYPES.indexOf("weeks")+1)
			);
	
	public static final List<String> INSTALL_RQLITE_SCRIPT_LINES() {
		return Arrays.asList(
				"ps aux | grep -ie rqlite | awk '{print $2}' | xargs kill -9",
				"cd " + DataSources.HOME_DIR(),
				"mkdir db",
				"cd db/",
				"export GOPATH=$PWD",
				"go get github.com/otoolep/rqlite",
				"go get gopkg.in/check.v1;");
	}
	


	public static final List<String> RQLITE_JOIN_LINES() {
		 return Arrays.asList(
					"ps aux | grep -ie rqlite | awk '{print $2}' | xargs kill -9",
					"cd " + DataSources.RQL_DIR(),
					"export GOPATH=$PWD",
					"$GOPATH/bin/rqlite -join " + DataSources.MASTER_NODE_URL() + 
					" -p " + DataSources.RQL_PORT + " data");
	}
			

	public static final List<String> RQLITE_STARTUP_SCRIPT_LINES() {
		return Arrays.asList(
				"ps aux | grep -ie rqlite | awk '{print $2}' | xargs kill -9",
				"cd " + DataSources.RQL_DIR(),
				"export GOPATH=$PWD",
				"$GOPATH/bin/rqlite -s 50 -p " + DataSources.RQL_PORT + " data");
	}

}

