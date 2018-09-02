package com.shxy.www.util;

import java.util.Map;
import java.util.Set;

public class MapUtil extends ObjectUtil {
	/**
	 * 比较map数组中对应键值和传入的对象是否相等
	 * @param source 源数据Map
	 * @param key 比较的键
	 * @param value 需要比较的值
	 * @return
	 * 		true = 不相等
	 * 		false= 相等
	 */
	public static boolean compareKeyValue(Map source, Object key, Object value){
		if(source==null||source.isEmpty()){
			return true;
		}
		if(!containsKey(source, key)){
			return true;
		}
		if(arrIsNULL(value)){
			return true;
		}
		Object obj = get(source, key);
		return !compare2Obj(obj, value);
	}
	
	/**
	 * 判断Map中是否存在指定的键
	 * @param 
	 * 		ht 被检查Map
	 * 		params 需要判断的键值列对
	 * @return
	 * 		false= ht中不满足指定的键
	 * 		true = ht满足指定的键
	 */
	public static boolean checkMapParams(Map ht, String ...params){
		if(ht==null||ht.isEmpty()){
			return false;
		}
		if(params.length>0){
			for(String param:params){
				if(!containsKey(ht, param)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 排查Map中是否存在为空的键
	 * @param
	 * 		source 被检查Map
	 * 		params 需要比较的键值列对
	 * @return
	 * 		false	: source某个的键值为空或不存在
	 * 		true	: source中不存在为空的键
	 */
	public static boolean checkMapParamsNotNULL(Map source, String ...params){
		if(source==null||source.isEmpty()){
			return false;
		}
		if(params.length>0){
			for(String param:params){
				if(!containsKey(source, param)){
					return false;
				}
				Object obj = get(source, param);
				if(obj==null){
					return false;
				}
				if(obj instanceof String){
					if(obj.equals("")){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 获得Map中的对象
	 * @param source 数据源
	 * @param key 要获得的对象的Key
	 * @return
	 */
	public static Object get(Map source, Object key){
		if(mapIsNull(source)){
			return "";
		}
		if(source.containsKey(key)){
			return source.get(key);
		}
		if(key instanceof String){
			if(source.containsKey(key.toString().toLowerCase())){
				return source.get(key.toString().toLowerCase());
			}else if(source.containsKey(key.toString().toUpperCase())){
				return source.get(key.toString().toUpperCase());
			}
		}
		return "";
	}
	
	/**
	 * 判断Map中是否存在指定的Key
	 * @param source
	 * @param key
	 * @return
	 * 		false = 不存�?
	 * 		true  = 存在
	 */
	public static boolean containsKey(Map source, Object key){
		if(mapIsNull(source)){
			return false;
		}
		if(objIsNull(key)){
			return false;
		}
		if(key instanceof String){
			if(source.containsKey(key.toString().toLowerCase())){
				return true;
			}else if(source.containsKey(key.toString().toUpperCase())){
				return true;
			}else if(source.containsKey(key.toString())){
				return true;
			}
		}else{
			if(source.containsKey(key)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断Map是否为空
	 * @param source
	 * @return
	 * 		true=map为空或为null
	 */
	public static boolean mapIsNull(Map source){
		if(source==null||source.isEmpty()){
			return true;
		}
		Set key = source.keySet();
		boolean nullFlag = true;
		for(Object obj:key){
			Object keyValue = source.get(obj);
			if(keyValue!=null && !keyValue.equals("")){
				nullFlag = false;
			}
		}
		return nullFlag;
	}
}
