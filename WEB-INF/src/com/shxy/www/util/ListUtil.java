package com.shxy.www.util;

import java.util.List;

public class ListUtil extends ObjectUtil {
	
	/**
	 * 判断List是否为null
	 * @param params
	 * @return
	 * 		true==List为空
	 * 		false==List不为空
	 */
	public static boolean listIsNull(List... params){
		if(params==null){
			return true;
		}
		for(int i=0; i<params.length; i++){
			if(params[i]==null||params[i].isEmpty()){
				return true;
			}
		}
		return false;
	}
	
}
