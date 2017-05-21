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

import java.util.Locale;
import java.util.regex.Pattern;

import java.math.BigInteger;

import static com.knowgate.stringutils.Str.removeChars;

import java.math.BigDecimal;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * <p>Combination of BigDecimal with Currency Sign</p>
 * This class handles money amounts that include a currency sign
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class Money extends BigDecimal {

  private static final long serialVersionUID = 1l;

  private static final DecimalFormat FMT2 = new DecimalFormat();
  
  private static final Pattern CurrencyStr = Pattern.compile("(\\+|-)?([0-9]+)|([0-9]+.[0-9]+)");

  private CurrencyCode oCurrCode;

  // ---------------------------------------------------------------------------

  /**
   * @param sVal String
   * @throws UnsupportedOperationException
   */
  private Money(String sVal) throws UnsupportedOperationException {
    super(sVal);
    throw new UnsupportedOperationException("Money(String) is not an allowed constructor");
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor that makes a copy from another Money value
   * @param oVal Money
   */
  public Money(Money oVal) {
    super(((BigDecimal) oVal).toString());
    oCurrCode = oVal.oCurrCode;
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param sVal String Numeric value in US decimal format (using dot as decimal delimiter)
   * @param oCur CurrencyCode
   * @throws NumberFormatException
   */
  public Money(String sVal, CurrencyCode oCur) throws NumberFormatException {
    super(sVal);
    oCurrCode = oCur;
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param sVal String Numeric value in US decimal format (using dot as decimal delimiter)
   * @param sCur String Currency alphanumeric code {"USD", "EUR", etc.}
   * @throws NumberFormatException
   * @throws IllegalArgumentException
   */
  public Money(String sVal, String sCur)
    throws NumberFormatException, IllegalArgumentException {
    super(sVal);
    oCurrCode = CurrencyCode.currencyCodeFor(sCur);
    if (null==oCurrCode) throw new IllegalArgumentException("Money() "+sCur+" is not a legal currency alphanumeric code");
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param dVal double
   * @param oCur CurrencyCode
   */
  public Money(double dVal, CurrencyCode oCur) {
    super(dVal);
    oCurrCode = oCur;
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param dVal double
   * @param sCur String Currency alphanumeric code {"USD", "EUR", etc.}
   * @throws IllegalArgumentException if currency code is not recognized 
   */
  public Money(double dVal, String sCur) throws IllegalArgumentException {
    super(dVal);
    oCurrCode = CurrencyCode.currencyCodeFor(sCur);
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param oVal BigDecimal
   * @param sCur String Currency alphanumeric code {"USD", "EUR", etc.}
   * @throws IllegalArgumentException if currency code is not recognized 
   */
  public Money(BigDecimal oVal, String sCur) {
    super(oVal.toString());
    oCurrCode = CurrencyCode.currencyCodeFor(sCur);
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param oVal BigDecimal
   * @param oCur CurrencyCode
   */
  public Money(BigDecimal oVal, CurrencyCode oCur) {
    super(oVal.toString());
    oCurrCode = oCur;
  }
  
  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param oVal BigInteger
   * @param oCur CurrencyCode
   */
  public Money(BigInteger oVal, CurrencyCode oCur) {
    super(oVal);
    oCurrCode = oCur;
  }

  // ---------------------------------------------------------------------------

  /**
   * Constructor
   * @param oVal BigInteger
   * @param sCur String Currency alphanumeric code {"USD", "EUR", etc.}
   * @throws IllegalArgumentException if currency code is not recognized 
   */
  public Money(BigInteger oVal, String sCur) {
    super(oVal);
    oCurrCode = CurrencyCode.currencyCodeFor(sCur);
  }
  
  // ---------------------------------------------------------------------------

  public CurrencyCode currencyCode() {
    return oCurrCode;
  }

  // ---------------------------------------------------------------------------

   /**
    * Compare to money amounts with the same currency
    * @param oMny Money
    * @return int Zero if this amount is equal to given amount.
    * Less than zero if if this amount is less than given amount.
    * More than zero if if this amount is greater than given amount.
    * @throws IllegalArgumentException If currency codes are not the same
    */
   public int compareTo(Money oMny) throws IllegalArgumentException {
    if (currencyCode().equals(oMny.currencyCode()))
      return super.compareTo(oMny);
    else
      throw new IllegalArgumentException("Currency code "+currencyCode()+" does not match "+oMny.currencyCode());
   } // compareTo

  // ---------------------------------------------------------------------------

   /**
    * Maximum of this Money instance and given Money instance
    * @param oMny Money
    * @return Money
    */
   public Money max(Money oMny) {
	  return compareTo(oMny)<0 ? oMny : this;
   } // max

  // ---------------------------------------------------------------------------

   /**
    * Minimum of this Money instance and given Money instance
    * @param oMny Money
    * @return Money
    */
   public Money min(Money oMny) {
	  return compareTo(oMny)<0 ? this : oMny;
   } // max
   	
  // ---------------------------------------------------------------------------

  /**
   * <p>Add two money amounts</p>
   * The return value is in the currency of this object.
   * If the added amount does not have the same currency
   * then it is converted by calling a web service before performing addition.
   * The scale of the returned value is max(this.scale(), oMny.scale()).
   * @return this.value + (to this currency) oMny.value
   * @throws NullPointerException If oMny is null
   * @throws IllegalArgumentException If currency codes are not the same
   */
  public Money add(Money oMny) throws NullPointerException, IllegalArgumentException {
    if (oMny.signum()==0)
      return new Money(this);
    else if (currencyCode().equals(oMny.currencyCode()))
      return new Money (super.add(oMny),currencyCode());
    else
      throw new IllegalArgumentException("Currency code "+currencyCode()+" does not match "+oMny.currencyCode());
  } // add

  // ---------------------------------------------------------------------------

  /**
   * <p>Subtract two money amounts</p>
   * The return value is in the currency of this object.
   * If the added amount does not have the same currency
   * then it is converted by calling a web service before performing addition.
   * The scale of the returned value is max(this.scale(), oMny.scale()).
   * @return this.value - (to this currency) oMny.value
   * @throws NullPointerException If oMny is null
   * @throws IllegalArgumentException If currency codes are not the same
   */
  public Money subtract(Money oMny) throws NullPointerException {
    if (oMny.signum()==0)
      return new Money(this);
    else if (currencyCode().equals(oMny.currencyCode()))
      return new Money (super.subtract(oMny),currencyCode());
    else
      throw new IllegalArgumentException("Currency code "+currencyCode()+" does not match "+oMny.currencyCode());
  } // subtract
  	
  // ---------------------------------------------------------------------------

  /**
   * <p>Checks whether the given string can be parsed as a valid Money value</p>
   * Both comma and dot are allowed as either thousands or decimal delimiters.
   * If there is only a comma or a dot then it is assumed to be de decimal delimiter.
   * If both comma and dot are present, then the leftmost of them is assumed to
   * be the thousands delimiter and the rightmost is the decimal delimiter.
   * Any letters and currency symbols {€$£¤¢¥#ƒ&amp;} are ignored
   * @param sVal String
   * @return boolean
   */
  public static boolean isMoney (String sVal) {
    if (sVal==null) return false;
    if (sVal.length()==0) return false;
    String sAmount = sVal.toUpperCase();
    int iDot = sAmount.indexOf('.');
    int iCom = sAmount.indexOf(',');
    if (iDot!=0 && iCom!=0) {
      if (iDot>iCom) {
    	sAmount.replace(",", "");
      } else {
      	sAmount.replace(".", "");
      }
    } // fi
    sAmount = sAmount.replace(',','.');
    sAmount = removeChars(sAmount, "€$£¤¢¥#ƒ& ABCDEFGHIJKLMNOPQRSZUVWXYZ");
    return CurrencyStr.matcher(sAmount).matches();
  } // isMoney

  // ---------------------------------------------------------------------------

  /**
   * <p>Create a Money instance from a figure and currency</p>
   * @param sVal String Must be of the form Currency Code + Figure or Figure + Currency Code. For example "23,5 EUR" or "GBP 12.8"
   * The figure may contain dot or comma as a decimal delimiter. € $ £ and ¥ are also recognized as currency codes
   * @return Money
   * @throws NullPointerException If String parameter is <b>null</b>
   * @throws IllegalArgumentException If String parameter is an empty String
   * @throws NumberFormatException
   */
  public static Money parse(String sVal)
    throws NullPointerException,IllegalArgumentException,NumberFormatException {
    int iDot, iCom;
    CurrencyCode oCur = null;
    String sAmount;

    if (null==sVal) throw new NullPointerException("Money.parse() argument cannot be null");
    if (sVal.length()==0) throw new IllegalArgumentException("Money.parse() argument cannot be an empty string");

    sAmount = sVal.toUpperCase();
    if (sAmount.indexOf("EUR")>=0 || sAmount.indexOf("€")>=0 || sAmount.indexOf("&euro;")>=0)
      oCur = CurrencyCode.EUR;
    else if (sAmount.indexOf("USD")>=0 || sAmount.indexOf("$")>=0)
      oCur = CurrencyCode.USD;
    else if (sAmount.indexOf("GBP")>=0 || sAmount.indexOf("£")>=0)
      oCur = CurrencyCode.GBP;
    else if (sAmount.indexOf("JPY")>=0 || sAmount.indexOf("YEN")>=0 || sAmount.indexOf("¥")>=0)
      oCur = CurrencyCode.JPY;
    else if (sAmount.indexOf("CNY")>=0 || sAmount.indexOf("YUAN")>=0)
      oCur = CurrencyCode.CNY;

    iDot = sAmount.indexOf('.');
    iCom = sAmount.indexOf(',');

    if (iDot!=0 && iCom!=0) {
      if (iDot>iCom) {
    	sAmount.replace(",", "");
      } else {
    	sAmount.replace(".", "");
      }
    } // fi

    sAmount = sAmount.replace(',','.');
    sAmount = removeChars(sAmount, "€$£¤¢¥#ƒ& ABCDEFGHIJKLMNOPQRSZUVWXYZ");

    return  new Money(sAmount, oCur);
  } // parse

  // ---------------------------------------------------------------------------

  /**
   * Rounds a BigDecimal value to two decimals
   * @return BigDecimal
   */
  public Money round2 () {
    FMT2.setMaximumFractionDigits(2);    
    return new Money (FMT2.format(doubleValue()).replace(',', '.'), oCurrCode);
  }

  // ---------------------------------------------------------------------------

  /**
   * <p>Convert <b>this</b> money to another currency</p>
   * @param oTarget Target CurrencyCode
   * @param oRatio BigDecimal Conversion ratio
   * @return Money if <b>this</b> CurrencyCode is the same as oTarget
   * then <b>this</b> is returned without any modification,
   * if if <b>this</b> CurrencyCode is different from oTarget
   * then the returned value is <b>this</b> multiplied by oRatio.
   * @throws NullPointerException if oTarget is <b>null</b>
   */
  public Money convertTo (CurrencyCode oTarget, BigDecimal oRatio)
    throws NullPointerException {

    Money oNewVal;

    if (oTarget==null) throw new NullPointerException("Money.convertTo() target currency cannot be null");
    if (oRatio==null) throw new NullPointerException("Money.convertTo() conversion ratio cannot be null");

    if (oCurrCode!=null) {
      if (oCurrCode.equals(oTarget))
        oNewVal = this;
      else
        oNewVal = new Money(multiply(oRatio), oTarget);
    } else {
      oNewVal = new Money(multiply(oRatio), oTarget);
    }
    return oNewVal;
  } // convertTo

  // ---------------------------------------------------------------------------

  /**
   * <p>Convert <b>this</b> money to another currency</p>
   * @param sTarget Target CurrencyCode
   * @param oRatio BigDecimal Conversion ratio
   * @return Money if <b>this</b> CurrencyCode is the same as oTarget
   * then <b>this</b> is returned without any modification,
   * if if <b>this</b> CurrencyCode is different from oTarget
   * then the returned value is <b>this</b> multiplied by oRatio.
   * @throws NullPointerException if oTarget is <b>null</b>
   */
  public Money convertTo (String sTarget, BigDecimal oRatio)
    throws NullPointerException,IllegalArgumentException {

    if (sTarget==null) throw new NullPointerException("Money.convertTo() target currency cannot be null");

    oCurrCode = CurrencyCode.currencyCodeFor(sTarget);
    if (null==oCurrCode) throw new IllegalArgumentException("Money.convertTo() "+sTarget+" is not a legal currency alphanumeric code");

	return convertTo(oCurrCode, oRatio);
  } // convertTo

  // ---------------------------------------------------------------------------

 /**
  * Format a BigDecimal as a String following the rules for an specific language and country
  * @param sLanguage String ISO-639 two letter language code
  * @param sCountry String ISO-3166 two leter country code
  * @return String
  */
 public String format (String sLanguage, String sCountry) {

    Locale oLoc;
    
    if (null==sCountry) sCountry = oCurrCode.countryCode();

    if (null!=sLanguage && null!=sCountry)
      oLoc = new Locale(sLanguage,sCountry);
    else if (null!=sLanguage)
      oLoc = new Locale(sLanguage);
    else
      oLoc = Locale.getDefault();
    NumberFormat oFmtC = NumberFormat.getCurrencyInstance(oLoc);
    oFmtC.setCurrency(currencyCode().currency());
    return oFmtC.format(doubleValue());
  } // format

  // ---------------------------------------------------------------------------

 /**
  * @return String BigDecimal.toString() followed by a single space and currencyCode().alphacode()
  */
  public String toLocaleString () {
    if (oCurrCode==null)
      return super.toString();
    else
     return super.toString()+" "+oCurrCode.alphaCode();
  }

  // ---------------------------------------------------------------------------

}
