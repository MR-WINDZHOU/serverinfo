package com.www.zjf.model;

import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.util.HtmlUtils;

import com.shxy.www.conf.AbstractDBOptionDAO;
import com.shxy.www.conf.DBManager;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.StringUtil;
import com.shxy.www.util.sql.SQLAssemblyUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class DataManageModel {
	
	private DBManager dbManager = null;
	private AbstractDBOptionDAO dbOption = null;

	public DataManageModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
		this.dbOption = AbstractDBOptionDAO.getDBControl(dbManager);
	}
	
	/**
	 * 保存从form表单提交的信息(远程数据库)
	 * @param 
	 * 		strMap 文本控件提交的对象
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				record	: 插入的记录ID
	 * 			]
	 * @throws Exception
	 */
	public Map<String, String> saveSubmitInfo(Map<String, String> strMap) throws Exception{
		Hashtable<String, String> result = new Hashtable<String, String>();
		if(!MapUtil.checkMapParams(strMap)){//判断页面提交的参数是否为空
			result.put("result", "0");
			return result;
		}
		SQLAssemblyUtil sqlUtil = SQLAssemblyUtil.factory(this.dbManager);
		String sql = sqlUtil.map2SQL(strMap);//获得执行脚步
        if(sql.equals("")){//判断脚步是否为空
        	result.put("result", "0");
            return result;
        }
		if(strMap.containsKey("ID")){//更新
			if(this.dbManager.executeUpdateSQL(sql)>0){
				result.put("result", "1");
				return result;
			}
		}else{//插入
			String recordId = this.dbManager.executeInsertSQL(sql);
			if(!recordId.equals("-1")){//添加成功
				result.put("result", "1");
				result.put("record", recordId);
				return result;
			}
		}
		result.put("result", "2");
		return result;
	}
	
	/**
	 * 保存从form表单提交的信息
	 * @param 
	 * 		strMap 文本控件提交的对象
	 * 		fileMap 文件控件提交的对象
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				record	: 插入的记录ID
	 * 			]
	 * @throws Exception
	 */
	public String saveSqliteInfo(JSONObject colArray) throws Exception{
		String result = "0";
		String sql = " ";
		sql=" insert into customer(companyid,companyname,companystatus,remark)values('"+colArray.getString("xuhao")+"','"
				+ colArray.getString("customername")+"','"+colArray.getString("status")+"','"+colArray.getString("addnote")+"')";
		result = this.dbManager.executeInsertSQL(sql);
		if(!"-1".equals(result)){
			result = "1";
			return result;
		}
		
		return result;
	}
	
	/**
	 * 删除指定表中的数据(远程数据库)
	 * @param
	 * 		strMap 参数表
	 * 			[[type, 权限值]|...]
	 * @return
	 * 		执行结果Map[
	 * 			result	: 执行结果
	 * 			delnum	: 删除条数
	 * 		]
	 * @throws Exception 
	 */
	public Map<String, Object> delete(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TABLE", "RECORDS")){//判断参数中table和IDs是否为空
			result.put("result", "0");
			return result;
		}
		Map<String, List> fieldsMap = this.dbOption.fieldsInTable(strMap);
		if(!MapUtil.checkMapParamsNotNULL(fieldsMap)){//如果字段Map为空，返回
			result.put("result", "4");
			return result;
		}
		//delete语句
		String sql = "DELETE FROM param1 WHERE ID IN(param2)"
			.replaceAll("param1", strMap.get("TABLE"))
			.replaceAll("param2", StringUtil.str2QuoteStr(strMap.get("RECORDS")));
		int delNum = this.dbManager.executeUpdateSQL(sql);
		if(delNum<=0){//删除失败
			result.put("result", "2");
		}else{//删除成功
			result.put("result", "1");
			result.put("delnum", delNum+"");
		}
		return result;
	}
	
	/**
	 * 删除指定表中的数据(本地数据库Sqlite)
	 * @param
	 * 		strMap 参数表
	 * 			[[type, 权限值]|...]
	 * @return
	 * 		执行结果Map[[result, 执行结果]|...]
	 * @throws Exception 
	 */
	public Map<String, Object> sqliteDelete(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TABLE")){
			result.put("result", "0");
			return result;
		}
		//拼接where语句
		String whereSql = "1=1";
		Set<String> keySet = strMap.keySet();
		for(String s: keySet){
			if(s.startsWith("DELETEFIELD")&&MapUtil.checkMapParamsNotNULL(strMap, s, "DELETERECORDS"+s.replaceAll("DELETEFIELD", ""))){
				whereSql += " AND "+strMap.get(s)+" IN ("+StringUtil.str2QuoteStr(strMap.get("DELETERECORDS"+s.replaceAll("DELETEFIELD", "")))+")";
			}
		}
		//拼接删除语句SQL
		String sql = "DELETE FROM param1 WHERE param2"
			.replaceAll("param1", strMap.get("TABLE"))
			.replaceAll("param2", whereSql);
		int delNum = this.dbManager.executeUpdateSQL(sql);
		if(delNum<=0){//删除不成功
			result.put("result", "2");
		}else{//删除成功
			result.put("result", "1");
			result.put("delnum", delNum+"");//添加删除个数到result中
		}
		return result;
	}
	
	/**
	 * 检查指定表中的数据是否存在
	 * @param
	 * 		strMap 参数表
	 * 			[[type, 权限值]|...]
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				records	: 存在记录数
	 * 			]
	 * @throws Exception 
	 */
	public Map<String, Object> exist(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TABLE")){
			result.put("result", "0");
			return result;
		}
		Map<String, List> fieldsMap = this.dbOption.fieldsInTable(strMap);
		if(!MapUtil.checkMapParamsNotNULL(fieldsMap)){//判断数据库中该表中是否存在其他字段(除ID)
			result.put("result", "2");
			return result;
		}
		String whereSql = "";
		Set<String> keySet = strMap.keySet();
		for(String s: keySet){
			if(s.startsWith("CONDITION")){//查询是否存在condition内容
				if(fieldsMap.containsKey(strMap.get(s).toUpperCase())){
					whereSql += (StringUtil.arrIsNULL(whereSql)?"":" AND ")+strMap.get(s)+" IN("+StringUtil.str2QuoteStr(strMap.get("CONDITIONVALUE"+s.replace("CONDITION", "")))+")";
				}
			}
		}
		if(StringUtil.arrIsNULL(whereSql)){//where条件为空，返回
			result.put("result", "0");
			return result;
		}
		//查询记录个数
		String sql = "SELECT COUNT(1) FROM param1 WHERE param2"
			.replaceAll("param1", strMap.get("TABLE"))
			.replaceAll("param2", whereSql);
		List<Object> existInfo = this.dbManager.executeQuerySingleList(sql);
		result.put("result", "1");
		result.put("records", existInfo.get(0));
		return result;
	}
	
	/**
	 * 检查指定表中的数据是否存在
	 * @param
	 * 		strMap 参数表
	 * 			[[type, 权限值]|...]
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				records	: 存在记录数
	 * 			]
	 * @throws Exception 
	 */
	public Map<String, Object> existSqlite(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TABLE")){//判断参数中table是否为空
			result.put("result", "0");
			return result;
		}
		String whereSql = "";
		Set<String> keySet = strMap.keySet();
		for(String s: keySet){//循环Map键值
			if(s.startsWith("CONDITION")&&!s.startsWith("CONDITIONVALUE")){//判断参数是否是CONDITION?
				whereSql += (StringUtil.arrIsNULL(whereSql)?"":" AND ")+strMap.get(s)+" IN("+StringUtil.str2QuoteStr(strMap.get("CONDITIONVALUE"+s.replace("CONDITION", "")))+")";
			}
		}
		if(StringUtil.arrIsNULL(whereSql)){//判断是否存在where条件 如果不存在 直接返回
			result.put("result", "0");
			return result;
		}
		//查询结果
		String sql = "SELECT COUNT(1) FROM param1 WHERE param2"
			.replaceAll("param1", strMap.get("TABLE"))
			.replaceAll("param2", whereSql);
		List<Object> existInfo = this.dbManager.executeQuerySingleList(sql);
		result.put("result", "1");
		result.put("records", existInfo.get(0));
		return result;
	}
	
	/**
	 * 通用查询语句
	 * @param strMap 查询参数
	 * @return
	 * 		查询出的结果集
	 * @throws Exception
	 */
	public Map<String, Object> read(Map<String, String> strMap) throws Exception{
		Map<String, Object> readInfo = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TABLE", "ID")){//判断记录中table和ID是否为空
			return readInfo;
		}
		//table字段集合
		Map<String, List> fieldsInfo = this.dbOption.fieldsInTable(strMap);
		if(MapUtil.mapIsNull(fieldsInfo)){//判断数据库中该表中是否存在其他字段(除ID)
			return readInfo;
		}
		String whereSql = "ID="+StringUtil.strAddQuote(MapUtil.get(strMap, "id"));
		String sql = "";
		//拼接表字段
		Set<String> keySet = fieldsInfo.keySet();
		for(String s: keySet){
			sql += s+",";
		}
		//拼接SQL语句
		sql = "SELECT "+sql.substring(0, sql.length()-1)+" FROM "+MapUtil.get(strMap, "table");
		if(!StringUtil.arrIsNULL(whereSql)){
			sql += " WHERE "+ whereSql;
		}
		//查询结果
		List<Map<String, Object>> recordsInfo = this.dbManager.executeQueryMap(sql);
		if(!ListUtil.listIsNull(recordsInfo)){//如果存在记录 取出第一条记录
			readInfo = recordsInfo.get(0);
		}
		return readInfo;
	}
	
	/**
	 * 字段转换格式设置
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> operate(Map<String, String> strMap, Map<String, File> fileMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			result.put("result", "0");
			return result;
		}
		switch (ObjectUtil.obj2Int(MapUtil.get(strMap, "type"))) {
		case 1:
			result = this.clearForamt(strMap);
			break;
		case 2:
			result = this.fieldFormats(strMap);
			break;
		case 3://删除格式转换中的字段
			result = this.deleteFormatField(strMap);
			break;
		case 4://删除报表配置中的字段
			result = this.deleteConfigField(strMap);
			break;
		case 5://插入排序字段
			result = this.insertSortField(strMap);
			break;
		case 6://调整排序字段顺序
			result = this.changeSortOrders(strMap);
			break;
		case 7://改变转换格式
			result = this.changeFormat(strMap);
			break;
		case 8:
			result = this.tablesFilter(strMap);
			break;
		case 10://清空报表配置内容
			result = this.clearTempletConfig(strMap);
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * 清空报表配置信息
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> clearTempletConfig(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_045_ID")){
			result.put("result", "0");
			return result;
		}
		String sql = "DELETE FROM TAB_4006 WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_ID")));
		this.dbManager.executeUpdateSQL(sql);
		result.put("result", "1");
		return result;
	}
	
	
	/**
	 * 字段转换格式设置
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> sqliteOperate(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			result.put("result", "0");
			return result;
		}
		switch (ObjectUtil.obj2Int(MapUtil.get(strMap, "type"))) {
		case 1://保存项目修改路径
			result = this.saveEditUrls(strMap);
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * 保存项目修改路径
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> saveEditUrls(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "S001", "AUTHOR", "S003")){//判断用户ID，项目类型，项目路径是否为空
			result.put("result", "0");
			return result;
		}
		String[] ss = strMap.get("S001").split("\\n");//将项目路径按照换行进行切割
		String[] projects = strMap.get("S003").split(",");//切割项目名称
		String author = strMap.get("AUTHOR");
		String operate = strMap.get("S006");
		for(String s: ss){
			if(!s.replaceAll(" ", "").equals("")){//判断上传的项目路径是否为空
				for(String project: projects){
					//保存项目路径
					String sql = "INSERT INTO SYSTAB205(S001, S002, S003,S004,S006) VALUES(param1, param2, param3,param4,param5)"
						.replaceAll("param1", StringUtil.strAddQuote(s))
						.replaceAll("param2", StringUtil.strAddQuote(author))
						.replaceAll("param3", StringUtil.strAddQuote(project))
						.replaceAll("param4", StringUtil.strAddQuote(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()).toString()))
					    .replaceAll("param5", StringUtil.strAddQuote(operate));
					if(this.dbManager.executeInsertSQL(sql).equals("-1")){
						result.put("result", "2");
						return result;
					}
				}
			}
		}
		result.put("result", "1");
		return result;
	}
	
	/**
	 * 字段转换格式设置
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> tablesFilter(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "tab_045_id", "tab_051_001")){
			result.put("result", "0");
			return result;
		}
		String sql = "SELECT ID,TAB_051_002 FROM TAB_4012 WHERE TAB_045_ID=param1 AND TAB_051_001=param2"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "tab_045_id")))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "tab_051_001")));
		List<List> filterList = this.dbManager.executeQueryList(sql);
		result.put("result", "1");
		if(!ListUtil.listIsNull(filterList)){
			result.put("id", filterList.get(0).get(0));
			result.put("filter", filterList.get(0).get(1));
		}
		return result;
	}
	
	/**
	 * 修改字段转换内容
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> changeFormat(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_048_ID")){
			result.put("result", "0");
			return result;
		}
		String sql = "SELECT TAB_049_001,TAB_046_ID_01 FROM TAB_4010 WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "ID")));
		List<List> formatFlag = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(formatFlag)){
			result.put("result", "2");
			return result;
		}
		sql = "UPDATE TAB_4010 SET TAB_048_ID=param1 WHERE ID=param2"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "ID")))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_048_ID")));
		if(this.dbManager.executeUpdateSQL(sql)<0){
			result.put("result", "2");
			return result;
		}
		sql = "UPDATE TAB_4007 SET TAB_046_005='{formatparam2}' WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(formatFlag.get(0).get(1)))
			.replaceAll("param2", formatFlag.get(0).get(0)+"");
		if(this.dbManager.executeUpdateSQL(sql)>0){
			result.put("result", "1");
		}else{
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 向报表模板中插入排序字段
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> insertSortField(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap)){
			result.put("result", "0");
			return result;
		}
		strMap.put("TABLE", "TAB_4011");
		DataManageModel dataModel = new DataManageModel(this.dbManager);
		Map<String, String> insertResult = dataModel.saveSubmitInfo(strMap);
		if(MapUtil.compareKeyValue(insertResult, "result", "1")){
			result.put("result", "2");
			return result;
		}else{
			strMap.put("BEFORE", insertResult.get("record"));
			Map<String, Object> ordersResult = this.changeSortOrders(strMap);
			if(MapUtil.compareKeyValue(ordersResult, "result", "1")){
				result.put("result", "2");
				return result;
			}else{
				result.put("result", "1");
			}
		}
		return result;
	}
	
	/**
	 * 调整排序字段顺序
	 * @param strMap
	 * @return
	 * @throws Exception 
	 */
	private Map<String, Object> changeSortOrders(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "before")){
			result.put("result", "0");
			return result;
		}
		Map<String, Object> callParams = new Hashtable<String, Object>();
		callParams.put("CALLNAME", "{CALL P_REP_SORT_ORDER(?, ?, ?)}");
		List<Object> inParams = new ArrayList<Object>();
		inParams.add(strMap.get("BEFORE"));
		inParams.add(MapUtil.checkMapParamsNotNULL(strMap, "after")?strMap.get("AFTER"):"0");
		List<Object> outParams = new ArrayList<Object>();
		outParams.add(Types.NUMERIC);
		callParams.put("IN", inParams);
		callParams.put("OUT", outParams);
		Map<String, Object> callResult = this.dbManager.executePrepareCall(callParams);
		if(callResult.get("result").equals("1")&&MapUtil.containsKey(callResult, "OUT")){
			@SuppressWarnings("unchecked")
			List<Object> outCalls = (List<Object>) MapUtil.get(callResult, "OUT");
			if(ListUtil.listIsNull(outCalls)){
				result.put("result", "2");
			}else{
				result.put("result", outCalls.get(0));
			}
		}else{
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 查询字段转换格式内容
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> fieldFormats(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "tab046")){
			result.put("result", "0");
			return result;
		}
		String sql = "SELECT TAB_046_005 FROM TAB_4007 WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(strMap.get("TAB046")));
		List<List> transFormat = this.dbManager.executeQueryList(sql);
		result.put("result", "1");
		result.put("format", ListUtil.listIsNull(transFormat)||StringUtil.arrIsNULL(transFormat.get(0).get(0))?"":HtmlUtils.htmlUnescape(transFormat.get(0).get(0)+""));
		return result;
	}
	
	/**
	 * 报表配置 清空字段配置内容
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> clearForamt(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_046_ID")){
			result.put("result", "0");
			return result;
		}
		//删除寄主字段下的所有字段（不包含寄主字段）
		String sql = "DELETE FROM TAB_4007 WHERE ID IN(SELECT TAB_046_ID_01 FROM TAB_4010 WHERE TAB_046_ID=param1) AND ID<>param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
		this.dbManager.executeUpdateSQL(sql);
		//删除转换格式中该字段的内容
		sql = "DELETE FROM TAB_4010 WHERE TAB_046_ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
		this.dbManager.executeUpdateSQL(sql);
		//更新字段表中的转换格式等内容为空
		sql = "UPDATE TAB_4007 SET TAB_046_005=null,TAB_045_ID=null WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
		if(this.dbManager.executeUpdateSQL(sql)>0){
			result.put("result", "1");
		}else{
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 删除格式转换使用的字段
	 * @param strMap 页面提交参数
	 * 		Map[
	 * 			TAB_046_ID		: 寄主字段ID
	 * 			TAB_046_ID_01	: 删除的字段ID
	 * 			TAB_046_005		: 寄主字段转换格式
	 * 		]
	 * @return
	 * 	Map[
	 * 		result	: 执行结果
	 * 		(
	 * 			0	: 参数错误
	 * 			1	: 执行成功
	 * 			2	: 执行失败
	 * 		)
	 * 	]
	 * @throws Exception
	 */
	private Map<String, Object> deleteFormatField(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_046_ID", "TAB_046_ID_01")){//判断寄主字段ID和被删除字段ID是否为空
			result.put("result", "0");
			return result;
		}
		String tab046id = strMap.get("TAB_046_ID");
		String tab046id01 = strMap.get("TAB_046_ID_01");
		String sql = "";
		if(tab046id.equals(tab046id01)){//如果删除的是本字段 只需要清空字段转换格式
			//删除字段转换格式内容
			sql = "DELETE FROM TAB_4010 WHERE TAB_046_ID=param1 AND TAB_046_ID_01=param1"
				.replaceAll("param1", StringUtil.strAddQuote(tab046id));
			if(this.dbManager.executeUpdateSQL(sql)<1){
				result.put("result", "2");
				return result;
			}
			//更新本字段转换格式为空
			sql = "UPDATE TAB_4007 SET TAB_046_005=NULL WHERE ID=param1"
				.replaceAll("param1", StringUtil.strAddQuote(tab046id));
			this.dbManager.executeUpdateSQL(sql);
		}else{//删除的不是本字段
			//删除字段内容
			sql = "DELETE FROM TAB_4007 WHERE ID=param1"
				.replaceAll("param1", StringUtil.strAddQuote(tab046id01));
			if(this.dbManager.executeUpdateSQL(sql)<1){
				result.put("result", "2");
				return result;
			}
		}
		//更新寄主字段的内容
		sql = "UPDATE TAB_4007 SET TAB_046_005=param1 WHERE ID=param2"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_005")))
			.replaceAll("param2", StringUtil.strAddQuote(tab046id));
		if(this.dbManager.executeUpdateSQL(sql)>0){//删除成功
			result.put("result", "1");
		}else{//删除失败
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 删除报表配置的字段
	 * @param strMap[ID	: 字段主键ID]
	 * @return
	 * 	Map[
	 * 		result	: 执行结果
	 * 		(
	 * 			0	: 参数错误
	 * 			1	: 执行成功
	 * 			2	: 执行失败
	 * 		)
	 * 	]
	 * @throws Exception
	 */
	private Map<String, Object> deleteConfigField(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "ID")){
			result.put("result", "0");
			return result;
		}
		//删除配置的字段
		String sql = "DELETE FROM TAB_4007 WHERE ID IN (SELECT TAB_046_ID_01 FROM TAB_4010 WHERE TAB_046_ID=param1) OR ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "ID")));
		if(this.dbManager.executeUpdateSQL(sql)>0){//删除成功
			result.put("result", "1");
		}else{//删除失败
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 保存从form表单提交的信息
	 * @param 
	 * 		strMap 文本控件提交的对象
	 * 		fileMap 文件控件提交的对象
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				record	: 插入的记录ID
	 * 			]
	 * @throws Exception
	 */
	public Map<String, String> saveSqliteInfo(JSONArray colArray) throws Exception{
		Hashtable<String, String> result = new Hashtable<String, String>();
		JSONObject jsonobj = null;
		String sql = " ";
		for (int i = 0; i < colArray.size();i++ ){
			jsonobj = colArray.getJSONObject(i);
			String itemid = (String)jsonobj.get("itemid");
			String colname = (String)jsonobj.get("colname");
			String datavalue = (String)jsonobj.get("datavalue");
			if(!"".equals(itemid)&&itemid!=null){
				sql = " update serverinfo set datavalue='"+datavalue+"' where colname='"+colname+"' and itemid="+itemid ;
				this.dbManager.executeUpdateSQL(sql);
				if(this.dbManager.executeUpdateSQL(sql)>0){
					result.put("result", "1");
					return result;
				}
			}
		}
		result.put("result", "2");
		return result;
	}
	
}