package com.shxy.www.util.sql;

import java.util.List;
import java.util.Map;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.StringUtil;

/**
 * 提交表单时拼接SQL
 * @author 藕旺江
 *
 */
public class SQLAssemblyUtilOfOracle extends SQLAssemblyUtil{
	
	public SQLAssemblyUtilOfOracle(DBManager dbManager){
		super(dbManager);
	}
	
	public String map2SQL(Map<String, String> strMap) throws Exception{
		if(!MapUtil.checkMapParams(strMap, "TABLE")){//操作的数据库表是否存在 如果不存在 返回空字符串
			return "";
		}
		String table = strMap.get("TABLE");//获得操作的数据表
		//查询表所有字段信息
		String sql = "SELECT UPPER(COLUMN_NAME) FROM USER_TAB_COLUMNS WHERE UPPER(TABLE_NAME)=UPPER(param1)".replace("param1", StringUtil.strAddQuote(table));
		List<List> fieldsInfo = this.dbManager.executeQueryList(sql);
		
		String sql1 = "";
		String sql2 = "";
		if(strMap.containsKey("ID")){//如果存在ID，并且不为空表示是更新操作
			String recordId = strMap.get("ID");//获得更新的主键ID
			//遍历表字段信息，拼接更新语句
			for(int i=0; i<fieldsInfo.size(); i++){
				String fieldName = fieldsInfo.get(i).get(0).toString().toUpperCase();
				if(fieldName.equals("ID")||fieldName.equals("AUTHOR")){//过滤的字段
					continue;
				}
				
				if(strMap.containsKey(fieldName)){//如果
					sql1 += fieldName+"="+StringUtil.strAddQuote(strMap.get(fieldName))+",";
				}
			}
			if(StringUtil.arrIsNULL(sql1)){
				return "";
			}
			return "UPDATE "+table+" SET "+sql1.substring(0,sql1.length()-1)+" WHERE ID IN("+StringUtil.str2QuoteStr(recordId)+")";
		}else{
			for(int i=0; i<fieldsInfo.size(); i++){
				String fieldName = fieldsInfo.get(i).get(0).toString().toUpperCase();

				if(strMap.containsKey(fieldName)){
					sql1 += fieldName+",";
					sql2 += StringUtil.strAddQuote(strMap.get(fieldName))+",";
				}
			}
			if(StringUtil.arrIsNULL(sql1, sql2)){
				return "";
			}
			return "INSERT INTO "+table+"("+sql1.substring(0,sql1.length()-1)+") VALUES("+sql2.substring(0,sql2.length()-1)+")";
		}
	}
	
}
