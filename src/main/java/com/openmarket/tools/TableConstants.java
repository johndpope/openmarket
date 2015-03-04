package com.openmarket.tools;

import java.util.ArrayList;
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
}
