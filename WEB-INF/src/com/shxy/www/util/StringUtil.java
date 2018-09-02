package com.shxy.www.util;

import org.springframework.web.util.HtmlUtils;

/**
 * 字符串操作特殊类
 * @author 藕旺江
 */
public class StringUtil extends ObjectUtil{
	/**
	 * 将字符串数组转换为字符串
	 * @param arr 需要转换的数据源
	 * @return 拼接后的字符串
	 */
	public static String arr2Str(String[] arr){
		String temp = "" ;		// �ַ��?
		if(arr != null && arr.length > 0){
			for(int i = 0 ; i < arr.length ; i ++){
				temp = temp + arr[i] + "," ;
			}
		}
		return temp.substring(0, temp.length()-1) ;
	}
	
	/**
	 * 将字符串数组转换为字符串(将特殊字符转换为html标记)
	 * @param arr 需要转换的数据源
	 * @return 拼接后的字符串
	 */
	public static String arrary2String(String[] arr){
		String temp = "";
		if(arr!=null&&arr.length>0){
			for(int i=0; i<arr.length; i++){
				temp += HtmlUtils.htmlEscape(arr[i])+",";
			}
			return temp.substring(0, temp.length()-1);
		}
		return temp;
	}
	
	/**
	 * 将数字的字符串转换为Int整形
	 * @param str 数字的字符串
	 * @return 如果字符串不为数值形，则返回"0" 否则 返回转换后的对应整形
	 */
	public static int str2Int(String str){
		return StringUtil.obj2Int(str);
	}
	
	/**
	 * 字符串前后加双引�?
	 * @param str
	 * @return
	 */
	public static String strAddQuotes(Object obj){
		if (obj==null) {
			return null;
		}
		String str = String.valueOf(obj);
		if(str.equals("")){
			return "null";
		}
		return "\""+str+"\"";
	}
	
	/**
	 * 字符串前后加单引�?
	 * @param str
	 * @return
	 */
	public static String strAddQuote(Object obj){
		if (obj==null) {
			return null;
		}
		if(StringUtil.arrIsNULL(obj)){
			return "null";
		}
		return "'"+obj+"'";
	}
	
	/**
	 * 将字符串中�?,”转换为�?,'�?
	 * @param source
	 * @return
	 */
	public static String str2QuoteStr(String source){
		if(source==null||source.equals("")){
			return "''";
		}
		if(source.endsWith(",")){
			source = source.substring(0,source.length()-1);
		}
		return "'"+source.replaceAll(",", "','")+"'";
	}
	
}
