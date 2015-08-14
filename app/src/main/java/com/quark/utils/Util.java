package com.quark.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;

public class Util {
	/**
	 * @Description: 手机号码验证
	 * @author howe
	 * @date 2014-7-22 下午4:00:37
	 * 
	 */

	public static boolean isMobileNO(String mobiles) {
		if (mobiles == null || (mobiles.trim().equals(""))) {
			return false;
		}
		// 18几的字段全开 ("^((13[0-9])|(15[^4,\\D])|(18[0,1,2,3,5-9]))\\d{8}$");
		Pattern p = Pattern.compile("^((1))\\d{10}$");
		// Pattern p = Pattern
		// .compile("^((13[0-9])|(15[^4,\\D])|(18[^4,\\D]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 是否为数字
	public static boolean isNumeric(String str) {
		if (str == null || (str.trim().equals(""))) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	// 判断email格式是否正确
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 校验银行卡卡号
	 * 
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	// 身高为140cm-220cm
	// public static boolean heightCheck(String heightStr){
	// if(heightStr==null||(heightStr.trim().equals(""))){
	// return false;
	// }
	// Pattern p = Pattern.compile("^1[4-9][0-9]|200cm\b$");
	// Matcher m = p.matcher(heightStr);
	// return m.matches();
	// }
	public static boolean heightCheck(String heightStr) {
		if (heightStr == null || (heightStr.trim().equals(""))) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[0-9]*$");
		if (pattern.matcher(heightStr).matches()) {
			int hei = Integer.valueOf(heightStr);
			if ((hei > 140) && (hei < 200)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isUserName(String mobiles) {
		Pattern p = Pattern.compile("^[A-Za-z\u4e00-\u9fa5]{2,15}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 检查是否为空 为空返回false,不为空返回true
	 * 
	 * @return boolean
	 */
	public static boolean isEmpty(String str) {
		if (str != null && (!str.trim().equals(""))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查是否存在SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static String DateToString(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 通过年月日获取 年龄
	 * 
	 * @param brithday
	 * @return
	 */
	public static String getCurrentAgeByBirthdate(String brithday) {

		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = new Date();
		java.util.Date mydate = null;
		try {
			mydate = myFormatter.parse(brithday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000)
				+ 1;

		String year = new java.text.DecimalFormat("#").format(day / 365f);

		return year;
	}

	public static boolean isIdCard(String idcard) {
		// 定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）
		Pattern idNumPattern = Pattern
				.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		// 通过Pattern获得Matcher
		Matcher m = idNumPattern.matcher(idcard);

		return m.matches();
	}

	// 工作内容 10-200字
	public static boolean isInfook(String str) {
		if (str != null && (!str.trim().equals("")) && (str.length() > 10)
				&& (str.length() < 1000)) {
			return true;
		} else {
			return false;
		}
	}

	// 详细地址 4-20字
	public static boolean isAddressDetail(String str) {
		if (str != null && (!str.trim().equals("")) && (str.length() > 3)
				&& (str.length() < 21)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isName(String str){
		Pattern p = Pattern.compile("^[\\u4E00-\\u9FFF]+$");
		Matcher matcher = p.matcher(str);
		return matcher.matches();
	}




	//验证身份证号码是否合法
	public static boolean IDCardValidate(String IDStr){
		String errorInfo="";          //记录错误信息
		String []ValCode={"1","0","X","9","8","7","6","5","4","3","2"};          //校验码<身份证最后一位>
		String[] Wi={"7","9","10","5","8","4","2","1","6","3","7","9","10","5","8","4","2"};
		//Wi表示第i位置上的加权因子
		String Ai="";                 //第i位置上的身份证号码数字值
		//=============================号码的长度应为15位或18位==============================
		if(IDStr.length()!=18&&IDStr.length()!=15){
			errorInfo="身份证号码长度应为18位或16位!";
			return false;
		}
		//======================================End=========================================


		//============================数字    除最后一位都为数字================================
		if(IDStr.length()==18)
			Ai=IDStr.substring(0,17);
		if(IDStr.length()==15)
			Ai=IDStr.substring(0,6)+"19"+IDStr.substring(6,15);
		if(isNumeric2(Ai)==false){           //判断身份证号码除最后一位是否是全数字
			errorInfo="身份证15位号码都应为数字;18位号码除最后一位外,都应为数字!";
			return false;
		}
		//======================================End=========================================

		//=================================出生年月是否有效==================================
		String strYear=Ai.substring(6,10);       //年份
		String strMonth=Ai.substring(10,12);     //月份
		String strDate=Ai.substring(12,14);      //日期
		if(!isDate(strYear,strMonth,strDate)){
			errorInfo="身份证号码中出生日期不在合法的范围内!";
			return false;
		}
		//======================================End=========================================

		//==============================地区码(身份证前六位)字段是否有效======================
		Hashtable h=GetAreaCode();        //获得地区码表
		if(h.get(Ai.substring(0,2))==null){         //判断身份证的地区编码是否合法,不合法为null
			errorInfo="身份证地区编码错误!";
			return false;
		}
		//======================================End=========================================

		//===============================判断最后一位是否是数字==============================
		int totalMulAiWi=0;           //十七位本体码求和
		for(int i=0;i<17;i++){
			totalMulAiWi+=Integer.parseInt(String.valueOf(Ai.charAt(i)))*Integer.parseInt(Wi[i]);
		}
		int modValue=totalMulAiWi%11;        //模
		String verityCode=ValCode[modValue];          //求得最后一位的校验码
		Ai+=verityCode;                               //加上校验码,最后得出完整的身份证号码
		if(IDStr.length()==18){
			if(Ai.equals(IDStr)==false){
				errorInfo="身份证号码无效,不是合法的身份证号码!";
				return false;
			}
		}
//        return "身份证号码合法!";
		return true;
		//=======================================End=========================================
	}


	//判断身份证中的出生日期是否合法
	public static boolean isDate(String strYear,String strMonth,String strDate){
		boolean judge=false;
		Date dt=new Date();
		int year=Integer.parseInt(strYear);
		int month=Integer.parseInt(strMonth);
		int date=Integer.parseInt(strDate);
		if(year>=1900&&year<=(dt.getYear()+1900)){
			if(year==dt.getYear()+1900){
				if(month>=1&&month<=dt.getMonth()+1){
					if(date>=1&&date<=dt.getDate())
						judge=true;        //日期合法
					else
						judge=false;        //日期出界
				}else{
					judge=false;
				}
			}else if(month>=1&&month<=12){
				switch(month){
					case 1:
					case 3:
					case 5:
					case 7:
					case 8:
					case 10:
					case 12:
						if(date>=1&&date<=31)
							judge=true;         //日期合法
						else
							judge=false;        //日期不合法
						break;
					case 2:
						if((year%100==0)||(year%4==0&&year%100!=0)){
							if(date>=1&&date<=29)
								judge=true;     //日期合法
							else
								judge=false;    //日期不合法
						}else{
							if(date>=1&&date<=28)
								judge=true;     //日期合法
							else
								judge=false;    //日期不合法
						}
						break;
					case 4:
					case 6:
					case 9:
					case 11:
						if(date>=1&&date<=30)
							judge=true;         //日期合法
						else
							judge=false;        //日期不合法
						break;
				}
			}else
				judge=false;   //月份超出范围
		}else{
			judge=false;       //年份超出范围
		}
		return judge;
	}
	//检查身份证号码除最后一位是否是数字
	public static boolean isNumeric2(String Ai){
		for(int i=0;i<Ai.length();i++){
			if(!(Ai.charAt(i)>='0'&&Ai.charAt(i)<='9'))
				return false;            //有非数字,返回错误
		}
		return true;                     //都为数字,返回真
	}
	//地区码参考表,用于身份证地区码的参考
	public static Hashtable GetAreaCode(){
		Hashtable hashtable=new Hashtable();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}
}


