package org.me.gcu.cw_currency;

import java.util.HashMap;
import java.util.Map;

public class CurrencyFlags {
    private static final Map<String, String> currencyToFlag = new HashMap<>();

    static {
        // Major currencies
        currencyToFlag.put("USD", "us");  // United States Dollar
        currencyToFlag.put("EUR", "eu");  // Euro
        currencyToFlag.put("GBP", "gb");  // British Pound
        currencyToFlag.put("JPY", "jp");  // Japanese Yen
        currencyToFlag.put("CHF", "ch");  // Swiss Franc
        currencyToFlag.put("CAD", "ca");  // Canadian Dollar
        currencyToFlag.put("AUD", "au");  // Australian Dollar
        currencyToFlag.put("NZD", "nz");  // New Zealand Dollar

        // Asia
        currencyToFlag.put("CNY", "cn");  // Chinese Yuan
        currencyToFlag.put("HKD", "hk");  // Hong Kong Dollar
        currencyToFlag.put("SGD", "sg");  // Singapore Dollar
        currencyToFlag.put("KRW", "kr");  // South Korean Won
        currencyToFlag.put("INR", "in");  // Indian Rupee
        currencyToFlag.put("MYR", "my");  // Malaysian Ringgit
        currencyToFlag.put("THB", "th");  // Thai Baht
        currencyToFlag.put("PHP", "ph");  // Philippine Peso
        currencyToFlag.put("IDR", "id");  // Indonesian Rupiah
        currencyToFlag.put("VND", "vn");  // Vietnamese Dong
        currencyToFlag.put("PKR", "pk");  // Pakistani Rupee
        currencyToFlag.put("BDT", "bd");  // Bangladeshi Taka
        currencyToFlag.put("LKR", "lk");  // Sri Lankan Rupee
        currencyToFlag.put("NPR", "np");  // Nepalese Rupee
        currencyToFlag.put("MMK", "mm");  // Myanmar Kyat
        currencyToFlag.put("KHR", "kh");  // Cambodian Riel
        currencyToFlag.put("LAK", "la");  // Lao Kip
        currencyToFlag.put("BND", "bn");  // Brunei Dollar
        currencyToFlag.put("TWD", "tw");  // Taiwan Dollar
        currencyToFlag.put("MOP", "mo");  // Macanese Pataca

        // Middle East
        currencyToFlag.put("AED", "ae");  // UAE Dirham
        currencyToFlag.put("SAR", "sa");  // Saudi Riyal
        currencyToFlag.put("QAR", "qa");  // Qatari Riyal
        currencyToFlag.put("KWD", "kw");  // Kuwaiti Dinar
        currencyToFlag.put("BHD", "bh");  // Bahraini Dinar
        currencyToFlag.put("OMR", "om");  // Omani Rial
        currencyToFlag.put("JOD", "jo");  // Jordanian Dinar
        currencyToFlag.put("ILS", "il");  // Israeli Shekel
        currencyToFlag.put("TRY", "tr");  // Turkish Lira
        currencyToFlag.put("IQD", "iq");  // Iraqi Dinar
        currencyToFlag.put("IRR", "ir");  // Iranian Rial
        currencyToFlag.put("LBP", "lb");  // Lebanese Pound
        currencyToFlag.put("SYP", "sy");  // Syrian Pound
        currencyToFlag.put("YER", "ye");  // Yemeni Rial

        // Europe
        currencyToFlag.put("NOK", "no");  // Norwegian Krone
        currencyToFlag.put("SEK", "se");  // Swedish Krona
        currencyToFlag.put("DKK", "dk");  // Danish Krone
        currencyToFlag.put("ISK", "is");  // Icelandic Króna
        currencyToFlag.put("PLN", "pl");  // Polish Zloty
        currencyToFlag.put("CZK", "cz");  // Czech Koruna
        currencyToFlag.put("HUF", "hu");  // Hungarian Forint
        currencyToFlag.put("RON", "ro");  // Romanian Leu
        currencyToFlag.put("BGN", "bg");  // Bulgarian Lev
        currencyToFlag.put("HRK", "hr");  // Croatian Kuna
        currencyToFlag.put("RSD", "rs");  // Serbian Dinar
        currencyToFlag.put("UAH", "ua");  // Ukrainian Hryvnia
        currencyToFlag.put("RUB", "ru");  // Russian Ruble
        currencyToFlag.put("BYN", "by");  // Belarusian Ruble
        currencyToFlag.put("MDL", "md");  // Moldovan Leu
        currencyToFlag.put("ALL", "al");  // Albanian Lek
        currencyToFlag.put("MKD", "mk");  // Macedonian Denar
        currencyToFlag.put("BAM", "ba");  // Bosnia-Herzegovina Mark

        // Americas
        currencyToFlag.put("MXN", "mx");  // Mexican Peso
        currencyToFlag.put("BRL", "br");  // Brazilian Real
        currencyToFlag.put("ARS", "ar");  // Argentine Peso
        currencyToFlag.put("CLP", "cl");  // Chilean Peso
        currencyToFlag.put("COP", "co");  // Colombian Peso
        currencyToFlag.put("PEN", "pe");  // Peruvian Sol
        currencyToFlag.put("VEF", "ve");  // Venezuelan Bolívar
        currencyToFlag.put("UYU", "uy");  // Uruguayan Peso
        currencyToFlag.put("PYG", "py");  // Paraguayan Guarani
        currencyToFlag.put("BOB", "bo");  // Bolivian Boliviano
        currencyToFlag.put("CRC", "cr");  // Costa Rican Colón
        currencyToFlag.put("GTQ", "gt");  // Guatemalan Quetzal
        currencyToFlag.put("HNL", "hn");  // Honduran Lempira
        currencyToFlag.put("NIO", "ni");  // Nicaraguan Córdoba
        currencyToFlag.put("PAB", "pa");  // Panamanian Balboa
        currencyToFlag.put("DOP", "do");  // Dominican Peso
        currencyToFlag.put("JMD", "jm");  // Jamaican Dollar
        currencyToFlag.put("TTD", "tt");  // Trinidad & Tobago Dollar
        currencyToFlag.put("BBD", "bb");  // Barbadian Dollar
        currencyToFlag.put("BSD", "bs");  // Bahamian Dollar
        currencyToFlag.put("BZD", "bz");  // Belize Dollar
        currencyToFlag.put("XCD", "ag");  // East Caribbean Dollar (using Antigua)
        currencyToFlag.put("AWG", "aw");  // Aruban Florin
        currencyToFlag.put("ANG", "cw");  // Netherlands Antillean Guilder
        currencyToFlag.put("SRD", "sr");  // Surinamese Dollar
        currencyToFlag.put("GYD", "gy");  // Guyanese Dollar

        // Africa
        currencyToFlag.put("ZAR", "za");  // South African Rand
        currencyToFlag.put("EGP", "eg");  // Egyptian Pound
        currencyToFlag.put("NGN", "ng");  // Nigerian Naira
        currencyToFlag.put("KES", "ke");  // Kenyan Shilling
        currencyToFlag.put("GHS", "gh");  // Ghanaian Cedi
        currencyToFlag.put("TZS", "tz");  // Tanzanian Shilling
        currencyToFlag.put("UGX", "ug");  // Ugandan Shilling
        currencyToFlag.put("ETB", "et");  // Ethiopian Birr
        currencyToFlag.put("MAD", "ma");  // Moroccan Dirham
        currencyToFlag.put("TND", "tn");  // Tunisian Dinar
        currencyToFlag.put("DZD", "dz");  // Algerian Dinar
        currencyToFlag.put("LYD", "ly");  // Libyan Dinar
        currencyToFlag.put("AOA", "ao");  // Angolan Kwanza
        currencyToFlag.put("BWP", "bw");  // Botswana Pula
        currencyToFlag.put("MUR", "mu");  // Mauritian Rupee
        currencyToFlag.put("MWK", "mw");  // Malawian Kwacha
        currencyToFlag.put("ZMW", "zm");  // Zambian Kwacha
        currencyToFlag.put("MZN", "mz");  // Mozambican Metical
        currencyToFlag.put("NAD", "na");  // Namibian Dollar
        currencyToFlag.put("SZL", "sz");  // Swazi Lilangeni
        currencyToFlag.put("LSL", "ls");  // Lesotho Loti

        // Oceania
        currencyToFlag.put("FJD", "fj");  // Fijian Dollar
        currencyToFlag.put("PGK", "pg");  // Papua New Guinea Kina
        currencyToFlag.put("WST", "ws");  // Samoan Tala
        currencyToFlag.put("TOP", "to");  // Tongan Paʻanga
        currencyToFlag.put("VUV", "vu");  // Vanuatu Vatu
        currencyToFlag.put("SBD", "sb");  // Solomon Islands Dollar

        // Other
        currencyToFlag.put("AFN", "af");  // Afghan Afghani
        currencyToFlag.put("AMD", "am");  // Armenian Dram
        currencyToFlag.put("AZN", "az");  // Azerbaijani Manat
        currencyToFlag.put("GEL", "ge");  // Georgian Lari
        currencyToFlag.put("KZT", "kz");  // Kazakhstani Tenge
        currencyToFlag.put("KGS", "kg");  // Kyrgyzstani Som
        currencyToFlag.put("TJS", "tj");  // Tajikistani Somoni
        currencyToFlag.put("TMT", "tm");  // Turkmenistani Manat
        currencyToFlag.put("UZS", "uz");  // Uzbekistani Som
        currencyToFlag.put("MNT", "mn");  // Mongolian Tögrög
        currencyToFlag.put("KPW", "kp");  // North Korean Won
        currencyToFlag.put("BTN", "bt");  // Bhutanese Ngultrum
        currencyToFlag.put("MVR", "mv");  // Maldivian Rufiyaa
    }

    /**
     * Get the flag resource name for a currency code
     * @param currencyCode The 3-letter currency code (e.g., "USD")
     * @return The flag resource name (e.g., "us") or null if not found
     */
    public static String getFlagForCurrency(String currencyCode) {
        return currencyToFlag.get(currencyCode.toUpperCase());
    }

    /**
     * Check if a flag exists for a currency code
     * @param currencyCode The 3-letter currency code
     * @return true if a flag mapping exists
     */
    public static boolean hasFlagForCurrency(String currencyCode) {
        return currencyToFlag.containsKey(currencyCode.toUpperCase());
    }
}