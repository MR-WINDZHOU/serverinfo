package com.shxy.www.util.sql;

import java.util.Map;
import java.util.Set;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.StringUtil;

/**
 * 提交表单时拼接SQL
 * @author 藕旺江
 *
 */
public class SQLAssemblyUtilOfSqlite extends SQLAssemblyUtil{
	
	public SQLAssemblyUtilOfSqlite(DBManager dbManager){
		super(dbManager);
	}
	
	public String map2SQL(Map<String, String> strMap) throws Exception{
		if(!MapUtil.checkMapParams(strMap, "TABLE")){//操作的数据库表是否存在 如果不存在 返回空字符串
			return "";
		}
		String table = strMap.get("TABLE");//获得操作的数据表
		strMap.remove("TABLE");//移除该参数，防止在之后的拼接中产生异常
		//查询表所有字段信息
		
		String sql1 = "";
		String sql2 = "";
		if(strMap.containsKey("ID")&&!StringUtil.arrIsNULL(strMap.get("ID"))){//如果存在ID，并且不为空表示是更新操作
			String recordId = strMap.get("ID");//获得更新的主键ID
			strMap.remove("ID");//移除
			
			Set<String> keySet = strMap.keySet();
			for(String s: keySet){
				sql1 += s+"="+StringUtil.strAddQuote(strMap.get(s))+",";//拼接更新的键值对条件
			}
			if(StringUtil.arrIsNULL(sql1)){//如果更新的语句为空，返回空字符串
				return "";
			}
			//返回拼接的更新语句
			return "UPDATE "+table+" SET "+sql1.substring(0,sql1.length()-1)+" WHERE ID IN("+StringUtil.str2QuoteStr(recordId)+")";
		}else{
			strMap.remove("ID");
			Set<String> keySet = strMap.keySet();
			for(String s: keySet){
				sql1 += s+",";//插入的表格字段
				sql2 += StringUtil.strAddQuote(strMap.get(s))+",";//对应的插入字段的值
			}
			if(StringUtil.arrIsNULL(sql1, sql2)){//如果字段和值都为空，返回空字符串
				return "";
			}
			//返回拼接的内容
			return "INSERT INTO "+table+"("+sql1.substring(0,sql1.length()-1)+") VALUES("+sql2.substring(0,sql2.length()-1)+")";
		}
	}
	
}
