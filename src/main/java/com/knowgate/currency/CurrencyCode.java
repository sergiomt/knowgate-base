package com.knowgate.currency;

/**
 * This file is licensed under the Apache License version 2.0.
 * You may not use this file except in compliance with the license.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
 */

import java.util.Arrays;
import java.util.Currency;
import java.util.Comparator;

/**
* <p>Currency Code</p>
* @author Sergio Montoro Ten
* @version 1.0
*/
public final class CurrencyCode {

private class CurrencyCodeComparator implements Comparator<CurrencyCode> {
  public int compare(CurrencyCode oCurr1, CurrencyCode oCurr2) throws NullPointerException {
    return oCurr1.toString().compareToIgnoreCase(oCurr2.toString());
  }
}

// ---------------------------------------------------------------------------

/**
 * <p>Constructor</p>
 * @param iNum int Currency Numeric Code
 * @param sAlpha String ISO 4217 3-letter code
 * @param sSign String "$", "€", "£", etc.
 * @param sId String ISO-639 two letter country code
 * @param sNm String Country Name
 * @param sTrEn String Currency Name (in English)
 * @throws NullPointerException if currencyCode is null
 * @throws IllegalArgumentException if currencyCode is not a supported ISO 4217 code.
 */
public CurrencyCode(int iNum, String sAlpha, String sSign, String sId, String sNm, String sTrEn)
  throws NullPointerException, IllegalArgumentException {
  iNumericCode = iNum;
  sAlphaCode = sAlpha;
  sSignCode = sSign;
  sIdEntity = sId;
  sNmEntity = sNm;
  sNmCurrencyEn = sTrEn;
  jCurrency = Currency.getInstance(sAlpha);
}

// ---------------------------------------------------------------------------

/**
 * Corresponding java.util.Currency object
 */
public Currency currency() {
  return jCurrency;
}

// ---------------------------------------------------------------------------

/**
 * @return String ISO-639 two letter country code in lower case
 */
public String countryCode() {
  return sIdEntity==null ? null : sIdEntity.toLowerCase();
}

// ---------------------------------------------------------------------------

/**
 * @return Three letter currency code in upper case
 */
public String alphaCode() {
  return sAlphaCode==null ? null : sAlphaCode.toUpperCase();
}

// ---------------------------------------------------------------------------

/**
 * @return String A single sign like $ € £ ¥
 */
public String singleCharSign() {
  return sSignCode;
}

// ---------------------------------------------------------------------------

/**
 * Currency name in English language
 * @return String
 */
public String currencyName() {
  return sNmCurrencyEn;
}

// ---------------------------------------------------------------------------

/**
 * @return int Currency numeric code
 */
public int numericCode() {
  return iNumericCode;
}

// ---------------------------------------------------------------------------

/**
 * Two currencies will be the same if the have the same alphaCode()
 * @param CurrencyCode
 * @return
 */
public boolean equals(CurrencyCode oCurCod) {
  if (sAlphaCode==null || oCurCod.alphaCode()==null)
    return false;
  else
    return sAlphaCode.equals(oCurCod.alphaCode());
}

//---------------------------------------------------------------------------

@Override
public int hashCode() {
	return sAlphaCode==null ? 0 : sAlphaCode.hashCode();
}

// ---------------------------------------------------------------------------

/**
 * @return String Currency three letter currency code in upper case.
 */
public String toString() {
  return sAlphaCode;
}

// ---------------------------------------------------------------------------

/**
 * Get CurrencyCode for a 3 letter currency identifier
 * @return CurrencyCode instance for given code or <b>null</b> if no currency was found for that code
 */
public static CurrencyCode currencyCodeFor (String sAlphaCode) {
  CurrencyCode oCurrCode;
  int iTableIndex;
  if (sAlphaCode!=null) {
    if (sAlphaCode.equalsIgnoreCase("EUR"))
      oCurrCode = EUR;
    else if (sAlphaCode.equalsIgnoreCase("USD"))
      oCurrCode = USD;
    else if (sAlphaCode.equalsIgnoreCase("GBP"))
      oCurrCode = GBP;
    else if (sAlphaCode.equalsIgnoreCase("JPY"))
      oCurrCode = JPY;
    else if (sAlphaCode.equalsIgnoreCase("CNY"))
      oCurrCode = CNY;
    else if (sAlphaCode.equalsIgnoreCase("RUB"))
      oCurrCode = RUB;
    else {
      oCurrCode = new CurrencyCode(0, sAlphaCode, null, null, null, null);
      iTableIndex = Arrays.binarySearch(Table, oCurrCode, oCurrCode.currencyCodeComparator());
      if (iTableIndex >= 0)
        oCurrCode = Table[iTableIndex];
      else
        oCurrCode = null;
    }
  } else {
    oCurrCode = null;
  }
  return oCurrCode;
} // currencyCodeFor

// ---------------------------------------------------------------------------

public static CurrencyCode currencyCodeFor (int iNumCode) {
  final int iCount = Table.length;
  CurrencyCode oCurrCode = null;
  if (iNumCode==978)
    oCurrCode=EUR;
  else if (iNumCode==840)
    oCurrCode=USD;
  else if (iNumCode==826)
    oCurrCode=GBP;
  else if (iNumCode==392)
    oCurrCode=JPY;
  else if (iNumCode==156)
    oCurrCode=CNY;
  else if (iNumCode==643)
    oCurrCode=RUB;
  else
  {
    for (int c=0; c<iCount && oCurrCode==null; c++) {
      if (Table[c].iNumericCode==iNumCode) oCurrCode=Table[c];
    } // next
  }
  return oCurrCode;
} // currencyCodeFor

// ---------------------------------------------------------------------------

private CurrencyCodeComparator currencyCodeComparator() {
  if (null==oCurrCodeComp) oCurrCodeComp = new CurrencyCodeComparator();
  return oCurrCodeComp;
}

// ---------------------------------------------------------------------------

public static final CurrencyCode CNY = new CurrencyCode(156,"CNY","¤","cn","China","Yuan Renminbi");
public static final CurrencyCode EUR = new CurrencyCode(978,"EUR","€","eec","European Economic Comunity","Euro");
public static final CurrencyCode GBP = new CurrencyCode(826,"GBP","£","uk","United Kingdom","Pound Sterling");
public static final CurrencyCode JPY = new CurrencyCode(392,"JPY","¥","jp","Japan","Yen");
public static final CurrencyCode USD = new CurrencyCode(840,"USD","$","us","United States","US Dollar");
public static final CurrencyCode RUB = new CurrencyCode(643,"RUB","R","ru","Russia","Russian Ruble");

// ---------------------------------------------------------------------------

private int iNumericCode;
private String sAlphaCode;
private String sSignCode;
private String sIdEntity;
private String sNmEntity;
private String sNmCurrencyEn;
private Currency jCurrency;

// ---------------------------------------------------------------------------

private CurrencyCodeComparator oCurrCodeComp = null;

// ---------------------------------------------------------------------------

private static final CurrencyCode[] Table = new CurrencyCode[] {
    new CurrencyCode(20,"ADP","₧","ad","Andorra","Andorran Peseta"),
    new CurrencyCode(784,"AED","¤","ae","United Arab Emirates","UAE Dirham"),
    new CurrencyCode(4,"AFA","¤","af","Afghanistan","Afghani"),
    new CurrencyCode(8,"ALL","¤","al","Albania","Lek"),
    new CurrencyCode(51,"AMD","¤","am","Armenia","Armenian Dram"),
    new CurrencyCode(532,"ANG","ƒ","an","Netherlands Antilles","Antillian Guilder"),
    //new CurrencyCode(982,"AOR","¤","ao","Angola","Kwanza Reajustado"),
    new CurrencyCode(32,"ARS","¤","ar","Argentina","Argentine Peso"),
    new CurrencyCode(40,"ATS","¤","as","Austria","Schilling"),
    new CurrencyCode(36,"AUD","$","au","Australia","Australian Dollar"),
    new CurrencyCode(533,"AWG","ƒ","aw","Aruba","Aruban Guilder"),
    new CurrencyCode(31,"AZM","¤","az","Azerbaijan","Azerbaijanian Manat"),
    new CurrencyCode(977,"BAM","¤","ba","Bosnia-Herzegovina","Convertible Marks"),
    new CurrencyCode(52,"BBD","$","bb","Barbados","Barbados Dollar"),
    new CurrencyCode(50,"BDT","¤","bd","Bangladesh","Taka"),
    new CurrencyCode(56,"BEF","¤","be","Belgium","Belgian Franc"),
    new CurrencyCode(975,"BGN","¤","bg","Bulgaria","Bulgarian Lev"),
    new CurrencyCode(48,"BHD","¤","bh","Bahrain","Bahraini Dinar"),
    new CurrencyCode(108,"BIF","¤","bi","Burundi","Burundi Franc"),
    new CurrencyCode(60,"BMD","¤","bm","Bermuda","Bermudian Dollar"),
    new CurrencyCode(96,"BND","¤","bn","Brunei Darussalam","Brunei Dollar"),
    new CurrencyCode(986,"BRL","¤","br","Brazil","Brazilian Real"),
    new CurrencyCode(44,"BSD","¤","bs","Bahamas","Bahamian Dollar"),
    new CurrencyCode(64,"BTN","¤","bt","Bhutan","Ngultrum"),
    new CurrencyCode(72,"BWP","¤",null,"Botswana","Pula"),
    new CurrencyCode(974,"BYR","¤","by","Belarus","Belarussian Ruble"),
    new CurrencyCode(84,"BZD","¤","bz","Belize","Belize Dollar"),
    new CurrencyCode(124,"CAD","¤","ca","Canada","Canadian Dollar"),
    new CurrencyCode(976,"CDF","¤","cg","Congo, The Democratic Republic Of","Franc Congolais"),
    new CurrencyCode(756,"CHF","₣","ch","Switzerland","Swiss Franc"),
    new CurrencyCode(152,"CLP","¤","cl","Chile","Chilean Peso"),
    CNY,
    new CurrencyCode(170,"COP","₱","co","Colombia","Colombian Peso"),
    new CurrencyCode(188,"CRC","¤","cr","Costa Rica","Costa Rican Colon"),
    new CurrencyCode(192,"CUP","₱","cu","Cuba","Cuban Peso"),
    new CurrencyCode(132,"CVE","¤","cv","Cape Verde","Cape Verde Escudo"),
    new CurrencyCode(196,"CYP","¤","cy","Cyprus","Cyprus Pound"),
    new CurrencyCode(203,"CZK","¤","cz","Czech Republic","Czech Koruna"),
    new CurrencyCode(280,"DEM","¤","nu","Germany","Deutsche Mark"),
    new CurrencyCode(262,"DJF","¤","dj","Djibouti","Djibouti Franc"),
    new CurrencyCode(208,"DKK","¤","dk","Denmark","Danish Krone"),
    new CurrencyCode(214,"DOP","₱","do","Dominican Republic","Dominican Peso"),
    new CurrencyCode(12,"DZD","¤","dz","Algeria","Algerian Dinar"),
    //new CurrencyCode(218,"ECS","¤","ec","Ecuador","Sucre"),
    new CurrencyCode(233,"EEK","¤","ee","Estonia","Kroon"),
    new CurrencyCode(818,"EGP","£","eg","Egypt","Egyptian Pound"),
    new CurrencyCode(232,"ERN","¤","nu","Eritrea","Nakfa"),
    new CurrencyCode(724,"ESP","₧","es","Spain","Spanish Peseta"),
    new CurrencyCode(230,"ETB","¤","et","Ethiopia","Ethiopian Birr"),
    EUR,
    new CurrencyCode(246,"FIM","¤","fi","Finland","Markka"),
    new CurrencyCode(242,"FJD","¤","fj","Fiji","Fiji Dollar"),
    new CurrencyCode(238,"FKP","¤","fk","Falkland Islands","Pound"),
    new CurrencyCode(250,"FRF","F","fr","France","French Franc"),
    GBP,
    new CurrencyCode(981,"GEL","¤","ge","Georgia","Lari"),
    new CurrencyCode(288,"GHC","¤","gh","Ghana","Cedi"),
    new CurrencyCode(292,"GIP","£","gi","Gibraltar","Gibraltar Pound"),
    new CurrencyCode(270,"GMD","¤","gm","Gambia","Dalasi"),
    new CurrencyCode(324,"GNF","F","gn","Guinea","Guinea Franc"),
    new CurrencyCode(300,"GRD","₯","gr","Greece","Drachma"),
    new CurrencyCode(320,"GTQ","¤","gt","Guatemala","Quetzal"),
    new CurrencyCode(624,"GWP","₱","gw","Guinea-Bissau","Guinea-Bissau Peso"),
    new CurrencyCode(328,"GYD","$","gy","Guyana","Guyana Dollar"),
    new CurrencyCode(344,"HKD","$","hk","Hong Kong","Hong Kong Dollar"),
    new CurrencyCode(340,"HNL","¤","hn","Honduras","Lempira"),
    new CurrencyCode(191,"HRK","¤","hr","Croatia","Kuna"),
    new CurrencyCode(332,"HTG","¤","ht","Haiti","Gourde"),
    new CurrencyCode(348,"HUF","ƒ","hu","Hungary","Forint"),
    new CurrencyCode(360,"IDR","₨","id","Indonesia","Rupiah"),
    new CurrencyCode(360,"IDR","₨","tp","East Timor","Rupiah"),
    new CurrencyCode(372,"IEP","£","ie","Ireland","Irish Pound"),
    new CurrencyCode(376,"ILS","₪","il","Israel","New Israeli Sheqel"),
    new CurrencyCode(356,"INR","₨","in","India","Indian Rupee"),
    new CurrencyCode(368,"IQD","¤","iq","Iraq","Iraqi Dinar"),
    new CurrencyCode(364,"IRR","¤","ir","Iran","Iranian Rial"),
    new CurrencyCode(352,"ISK","¤","is","Iceland","Iceland Krona"),
    new CurrencyCode(380,"ITL","£","it","Italy","Italian Lira"),
    new CurrencyCode(388,"JMD","$","jm","Jamaica","Jamaican Dollar"),
    new CurrencyCode(400,"JOD","¤","jo","Jordan","Jordanian Dinar"),
    JPY,
    new CurrencyCode(404,"KES","¤","ke","Kenya","Kenyan Shilling"),
    new CurrencyCode(417,"KGS","¤","kg","Kyrgyzstan","Som"),
    new CurrencyCode(116,"KHR","¤","kh","Cambodia, Kingdom of","Riel"),
    new CurrencyCode(174,"KMF","¤","km","Comoros","Comoro Franc"),
    new CurrencyCode(408,"KPW","₩",null,"Korea, Democratic People's Republic Of","North Korean Won"),
    new CurrencyCode(410,"KRW","₩",null,"Korea, Republic Of","Won"),
    new CurrencyCode(414,"KWD","¤","kw","Kuwait","Kuwaiti Dinar"),
    new CurrencyCode(136,"KYD","¤","ky","Cayman Islands","Cayman Islands Dollar"),
    new CurrencyCode(398,"KZT","¤","kz","Kazakhstan","Tenge"),
    new CurrencyCode(418,"LAK","₭","la","Lao People's Democratic Republic","Kip"),
    new CurrencyCode(422,"LBP","¤","lb","Lebanon","Lebanese Pound"),
    new CurrencyCode(144,"LKR","₨","lk","Sri Lanka","Sri Lanka Rupee"),
    new CurrencyCode(430,"LRD","¤","lr","Liberia","Liberian Dollar"),
    new CurrencyCode(426,"LSL","¤","ls","Lesotho","Loti"),
    new CurrencyCode(440,"LTL","¤","lt","Lithuania","Lithuanian Litas"),
    new CurrencyCode(442,"LUF","¤","lu","Luxembourg","Luxembourg Franc"),
    new CurrencyCode(428,"LVL","¤","lv","Latvia","Latvian Lats"),
    new CurrencyCode(434,"LYD","¤","ly","Libyan Arab Jamahiriya","Libyan Dinar"),
    new CurrencyCode(504,"MAD","¤","ma","Morocco","Moroccan Dirham"),
    new CurrencyCode(498,"MDL","¤","md","Republic Of Moldova","Moldovan Leu"),
    new CurrencyCode(450,"MGF","¤","mg","Madagascar","Malagasy Franc"),
    new CurrencyCode(807,"MKD","¤","mk","Macedonia, The Former Yugoslav Republic Of","Denar"),
    new CurrencyCode(104,"MMK","¤","mm","Myanmar","Kyat"),
    new CurrencyCode(496,"MNT","₮","mn","Mongolia","Tugrik"),
    new CurrencyCode(446,"MOP","¤","mo","Macau","Pataca"),
    new CurrencyCode(478,"MRO","¤","mr","Mauritania","Ouguiya"),
    new CurrencyCode(470,"MTL","¤","mt","Malta","Maltese Lira"),
    new CurrencyCode(480,"MUR","¤","mu","Mauritius","Mauritius Rupee"),
    new CurrencyCode(462,"MVR","¤","mv","Maldives","Rufiyaa"),
    new CurrencyCode(454,"MWK","¤","mw","Malawi","Kwacha"),
    new CurrencyCode(484,"MXN","₱","mx","Mexico","Mexican Peso"),
    new CurrencyCode(979,"MXV","¤","mx","Mexico","Mexican Unidad de Inversion (UDI)"),
    new CurrencyCode(458,"MYR","¤","my","Malaysia","Malaysian Ringgit"),
    new CurrencyCode(508,"MZM","¤","mz","Mozambique","Metical"),
    new CurrencyCode(516,"NAD","¤","na","Namibia","Namibia Dollar"),
    new CurrencyCode(566,"NGN","¤","ng","Nigeria","Naira"),
    new CurrencyCode(558,"NIO","¤","ni","Nicaragua","Cordoba Oro"),
    new CurrencyCode(528,"NLG","¤","nl","Netherlands","Netherlands Guilder"),
    new CurrencyCode(578,"NOK","¤","no","Norway","Norwegian Krone"),
    new CurrencyCode(524,"NPR","¤","np","Nepal","Nepalese Rupee"),
    new CurrencyCode(554,"NZD","¤","nz","New Zealand","New Zealand Dollar"),
    new CurrencyCode(512,"OMR","¤","om","Oman","Rial Omani"),
    new CurrencyCode(590,"PAB","¤","pa","Panama","Balboa"),
    new CurrencyCode(604,"PEN","¤","pe","Peru","Nuevo Sol"),
    new CurrencyCode(598,"PGK","¤","pg","Papua New Guinea","Kina"),
    new CurrencyCode(608,"PHP","₱","ph","Philippines","Philippine Peso"),
    new CurrencyCode(586,"PKR","¤","pk","Pakistan","Pakistan Rupee"),
    new CurrencyCode(985,"PLN","¤","pl","Poland","Zloty"),
    new CurrencyCode(620,"PTE","¤","pt","Portugal","Portuguese Escudo"),
    new CurrencyCode(600,"PYG","¤","py","Paraguay","Guarani"),
    new CurrencyCode(634,"QAR","¤","qa","Qatar","Qatari Rial"),
    new CurrencyCode(642,"ROL","¤","ro","Romania","Leu"),
    RUB,
    new CurrencyCode(810,"RUR","¤","ru","Russian Federation","Russian Ruble"),
    new CurrencyCode(646,"RWF","¤","rw","Rwanda","Rwanda Franc"),
    new CurrencyCode(682,"SAR","¤","sa","Saudi Arabia","Saudi Riyal"),
    new CurrencyCode(90,"SBD","¤","sb","Solomon Islands","Solomon Islands Dollar"),
    new CurrencyCode(690,"SCR","¤","sc","Seychelles","Seychelles Rupee"),
    new CurrencyCode(736,"SDD","¤","sd","Sudan","Sudanese Dinar"),
    new CurrencyCode(752,"SEK","¤","se","Sweden","Swedish Krona"),
    new CurrencyCode(702,"SGD","¤","sg","Singapore","Singapore Dollar"),
    new CurrencyCode(705,"SIT","¤","si","Slovenia","Tolar"),
    new CurrencyCode(703,"SKK","¤","si","Slovakia","Slovak Koruna"),
    new CurrencyCode(694,"SLL","¤","sl","Sierra Leone","Leone"),
    new CurrencyCode(706,"SOS","¤","so","Somalia","Somali Shilling"),
    new CurrencyCode(740,"SRG","¤","sr","Suriname","Surinam Guilder"),
    new CurrencyCode(678,"STD","¤","st","Saint Tome and Principe","Dobra"),
    new CurrencyCode(222,"SVC","¤","sv","El Salvador","El Salvador Colon"),
    new CurrencyCode(760,"SYP","¤","sy","Syrian Arab Republic","Syrian Pound"),
    new CurrencyCode(748,"SZL","¤","sz","Swaziland","Lilangeni"),
    new CurrencyCode(764,"THB","?","th","Thailand","Baht"),
    //new CurrencyCode(762,"TJR","¤","tj","Tadjikistan (Old)","Tajik Ruble (old)"),
    new CurrencyCode(972,"TJS","¤","tj","Tadjikistan","Somoni"),
    new CurrencyCode(795,"TMM","¤","tm","Turkmenistan","Manat"),
    new CurrencyCode(788,"TND","¤","tn","Tunisia","Tunisian Dinar"),
    new CurrencyCode(776,"TOP","¤","to","Tonga","Pa'anga"),
    new CurrencyCode(626,"TPE","¤","tp","East Timor","Timor Escudo"),
    new CurrencyCode(792,"TRL","£","tr","Turkey","Turkish Lira"),
    new CurrencyCode(780,"TTD","¤","tt","Trinidad and Tobago","Trinidad and Tobago Dollar"),
    new CurrencyCode(901,"TWD","¤","tw","Taiwan","New Taiwan Dollar"),
    new CurrencyCode(834,"TZS","¤","tz","United Republic Of Tanzania","Tanzanian Shilling"),
    new CurrencyCode(980,"UAH","¤","ua","Ukraine","Hryvnia"),
    new CurrencyCode(800,"UGX","¤","ug","Uganda","Uganda Shilling"),
    USD,
    new CurrencyCode(858,"UYU","¤","uy","Uruguay","Peso Uruguayo"),
    new CurrencyCode(860,"UZS","¤","uz","Uzbekistan","Uzbekistan Sum"),
    new CurrencyCode(862,"VEB","¤","ve","Venezuela","Bolivar"),
    new CurrencyCode(704,"VND","¤","vn","Vietnam","Dong"),
    new CurrencyCode(548,"VUV","¤","vu","Vanuatu","Vatu"),
    new CurrencyCode(882,"WST","¤","ws","Samoa","Tala"),
    new CurrencyCode(950,"XAF","¤","ga","Gabon/Guinea/Congo/Chad/Cameroon","CFA Franc BEAC"),
    new CurrencyCode(951,"XCD","$","gd","Caribbean Islands", "Caribbean Dollar"),
    new CurrencyCode(952,"XOF","¤","gw","Guinea-Bissau/Togo/Senegal/niger/Mali","CFA Franc BCEAO"),
    new CurrencyCode(953,"XPF","¤","pf","Polynesia/New Caledonia/Wallis","CFP Franc"),
    new CurrencyCode(999,"XXX","",null,null,"No currency"),
    new CurrencyCode(886,"YER","¤","ye","Yemen","Yemeni Rial"),
    new CurrencyCode(891,"YUM","¤","yu","Yugoslavia","New Dinar"),
    new CurrencyCode(710,"ZAR","¤","za","South Africa/Namibia/Lesotho","Rand"),
    new CurrencyCode(894,"ZMK","¤","zm","Zambia","Kwacha"),
    //new CurrencyCode(180,"ZRN","¤","zr","Zaire","New Zaire"),
    new CurrencyCode(716,"ZWD","$","zw","Zimbabwe","Zimbabwe Dollar")
};

}
