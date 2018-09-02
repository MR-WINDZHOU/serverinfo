package com.shxy.www.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.web.util.HtmlUtils;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.StringUtil;

public class GridDataModel {
	private DBManager dbManager = null;

	public GridDataModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}
	
	/**
	 * 获得Oracle数据库中的表信息
	 * @param strMap 页面提交信息
	 * 		type	：Grid类型
	 * @return
	 * 		返回对应Gird信息
	 * @throws Exception
	 */
	public Map<String, Object> gridData(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			return result;
		}
		switch (ObjectUtil.obj2Int(MapUtil.get(strMap, "type"))) {
		case 1://获得业务表格中的字段信息
			result = this.tableFields(strMap);
			break;
		case 2://转换类型列表信息
			result = this.typesFormats(strMap);
			break;
		case 4://报表中已配置的字段信息
			result = this.configFields(strMap);
			break;
		case 5://报表配置字段详细转换信息
			result = this.formatFields(strMap);
			break;
		case 6://获得业务表字段信息
			result = this.businessTableFieldsSingle(strMap);
			break;
		case 9:
			result = this.formatSelectFields(strMap);
			break;
		case 10://报表排序使用到的业务表和字段
			result = this.sortTableFields(strMap);
			break;
		case 11://获得报表已排序的字段信息
			result = this.sortConfigFields(strMap);
			break;
		case 12://查询报表配置中使用的表格
			result = this.configTables(strMap);
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * 转换类型及转换格式列表信息
	 * @param strMap
	 * @return
	 * 		Map[type_ZH:转换类型中文名, id:主键ID, name:转换格式中文名, view:预览效果]
	 * @throws Exception
	 */
	private Map<String, Object> typesFormats(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT T2.TAB_047_001,T1.ID,T1.TAB_048_001,T1.TAB_048_004 FROM TAB_4009 T1 LEFT JOIN TAB_4008 T2 ON T1.TAB_047_ID=T2.ID";
		List<List> formatsList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(formatsList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<formatsList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("type_ZH", formatsList.get(i).get(0));
			singleInfo.put("id", formatsList.get(i).get(1));
			singleInfo.put("name", formatsList.get(i).get(2));
			singleInfo.put("view", formatsList.get(i).get(3));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 获得本地数据库中的Grid信息
	 * @param strMap 页面提交参数信息
	 * 		type	: Grid类型
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> sqliteGridData(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			return result;
		}
		switch (ObjectUtil.obj2Int(MapUtil.get(strMap, "type"))) {
		case 1://读取用户列表信息(所有用户信息)
			result = this.usersList(strMap);
			break;
		case 2://读取已添加的项目类型
			result = this.projectTypes(strMap);
			break;
		case 3://获得项目修改路径
			result = this.projectUrls(strMap);
			break;
		case 4://常规操作选项
			result = this.operation();
			break;
		case 5://获得数据库IP列表
			result = this.getIPsList();
			break;
		case 6://获得连接状态信息
			result = this.connStatus(strMap);
			break;
		case 7://日期类型信息(业务配置XML中使用 项目中现在未使用)
			result = this.dateType(strMap);
			break;
		case 8://获得业务XML配置中已使用的功能方法列表(项目中现未使用)
			result = this.scriptFunction(strMap);
			break;
		case 9://获得报表调试时后台输出信息
			result = this.birtDebugControl(strMap);
			break;
		}
		return result;
	}
	
	/**
	 * 获得报表调试时后台输出信息
	 * @param strMap 页面提交信息
	 * 		tab045id	: 报表配置主键ID
	 * 		author		: 操作人
	 * @return
	 * 		Map[
	 * 			type	: 日志类型, 
	 * 			content	: 日志内容, 
	 * 			date	: 日志产生日期
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> birtDebugControl(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT S2.S002,S1.S004,S1.S005 FROM SYSTAB208 S1 LEFT JOIN SYSTAB209 S2 ON S1.S003=S2.S001 WHERE S1.S001 IN ('-1','param1') AND S1.S002=param2 ORDER BY S1.S005 DESC,S1.S001 DESC,S1.ID DESC"
				.replaceAll("param1", MapUtil.get(strMap, "tab045id")+"")
				.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "author")));
		List<List> debugsList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(debugsList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<debugsList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("type", debugsList.get(i).get(0));
			singleInfo.put("content", debugsList.get(i).get(1));
			singleInfo.put("date", debugsList.get(i).get(2));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 日期类型信息(业务配置XML中使用 项目中现在未使用)
	 * @param strMap 页面提交参数(未使用)
	 * @return
	 * 		Map[
	 * 			date_type	: 日期类型
	 * 			date_name	: 日期中文显示名
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> dateType(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT S001,S002 FROM SYSTAB206";
		List<List> typesList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(typesList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<typesList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("date_type", typesList.get(i).get(0));
			singleInfo.put("date_name", typesList.get(i).get(1));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 获得业务XML配置中已使用的功能方法列表(项目中现未使用)
	 * @param strMap(未使用)
	 * @return
	 * 		Map[
	 * 			name	: 方法中文名, 
	 * 			js		: 方法英文名
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> scriptFunction(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT S001,S002 FROM SYSTAB207";
		List<List> typesList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(typesList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<typesList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("name", typesList.get(i).get(0));
			singleInfo.put("js", typesList.get(i).get(1));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 获取项目修改路径
	 * @param strMap 页面提交参数
	 * 		STARTTIME	: 查询开始时间
	 * 		ENDTIME		: 查询结束时间
	 * 		QUERY		: 模糊匹配的字符串
	 * 		S002		: 用户ID
	 * 		S003		: 项目类型
	 * @return
	 * 		Map[
	 * 			id		: 主键ID, 
	 * 			url		: 项目路径, 
	 * 			project	: 修改的项目, 
	 * 			author	: 修改人, 
	 * 			time	: 路径提交时间,
	 *          operate :功能/项目名称
	 * 			update	: 文件是否已升级
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> projectUrls(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String whereSQL = "1=1";
		if(MapUtil.checkMapParamsNotNULL(strMap, "STARTTIME")){
			whereSQL += " AND S1.S004>=param1".replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "STARTTIME")+" 00:00:00"));
		}
		if(MapUtil.checkMapParamsNotNULL(strMap, "ENDTIME")){
			whereSQL += " AND S1.S004<=param1".replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "ENDTIME")+" 23:59:59"));
		}
		if(MapUtil.checkMapParamsNotNULL(strMap, "QUERY")){
			whereSQL += " AND (S2.S001 LIKE '%param1%' OR S3.S001 LIKE '%param1%' OR S1.S001 LIKE '%param1%' OR S1.S004 LIKE '%param1%')"
				.replaceAll("param1", StringUtil.obj2Str(MapUtil.get(strMap, "QUERY")));
		}
		if(MapUtil.checkMapParamsNotNULL(strMap, "S002")){
			whereSQL += " AND S1.S002 IN(param1)".replaceAll("param1", StringUtil.str2QuoteStr(StringUtil.obj2Str(MapUtil.get(strMap, "S002"))));
		}
		if(MapUtil.checkMapParamsNotNULL(strMap, "S003")){
			whereSQL += " AND S1.S003 IN(param1)".replaceAll("param1", StringUtil.str2QuoteStr(StringUtil.obj2Str(MapUtil.get(strMap, "S003"))));
		}
		
		String sql = "SELECT S1.ID,S1.S001,S2.S001,S3.S004,S1.S004,S1.S005,S1.S006 FROM SYSTAB205 S1 LEFT JOIN SYSTAB204 S2 ON S1.S003=S2.ID LEFT JOIN SYSTAB200 S3 ON S3.ID=S1.S002 WHERE "+whereSQL;
		List<List> typesList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(typesList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<typesList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("id", typesList.get(i).get(0));
			singleInfo.put("url", typesList.get(i).get(1));
			singleInfo.put("project", typesList.get(i).get(2));
			singleInfo.put("author", typesList.get(i).get(3));
			singleInfo.put("time", typesList.get(i).get(4));
			singleInfo.put("update", typesList.get(i).get(5));
			singleInfo.put("operate", typesList.get(i).get(6));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 读取已添加的项目类型
	 * @param strMap 页面提交的参数(此处未使用)
	 * @return
	 * 		Map[
	 * 			id		: 主键ID, 
	 * 			name	: 项目名
	 *		]
	 * @throws Exception
	 */
	private Map<String, Object> projectTypes(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT ID,S001 FROM SYSTAB204 WHERE S002='1'";
		List<List> typesList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(typesList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<typesList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("id", typesList.get(i).get(0));
			singleInfo.put("name", typesList.get(i).get(1));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 读取用户列表
	 * @param strMap 页面提交的参数(此处未使用)
	 * @return
	 * 		Map[
	 * 			id			: 主键ID, 
	 * 			name		: 登录名, 
	 * 			password	: 用户密码, 
	 * 			type		: 用户类型, 
	 * 			show		: 显示名
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> usersList(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT ID,S001,S002,S003,S004 FROM SYSTAB200";
		List<List> usersList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(usersList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<usersList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("id", usersList.get(i).get(0));
			singleInfo.put("name", usersList.get(i).get(1));
			singleInfo.put("password", usersList.get(i).get(2));
			singleInfo.put("type", usersList.get(i).get(3));
			singleInfo.put("show", usersList.get(i).get(4));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 报表配置
	 * 		查询报表配置中使用的表格
	 * @param strMap 页面提交的信息
	 * 		config	: TAB_045表ID
	 * @return
	 * 		Map[
	 * 			table		: 英文表名
	 * 			table_ZH	: 中文表名
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> configTables(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "config")){
			return result;
		}
		String sql = "SELECT T2.ZGA18001,T2.ZGA18003 FROM (SELECT tab_046_001 FROM TAB_4007 WHERE TAB_045_ID=param1 GROUP BY tab_046_001) T1 LEFT JOIN ZGA018 T2 ON T2.ZGA18001=T1.TAB_046_001 ORDER BY ZGA18001"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "config")));
		List<List> tablesList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tablesList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<tablesList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("table", tablesList.get(i).get(0));
			singleInfo.put("table_ZH", tablesList.get(i).get(1));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 获得业务单记录表字段信息
	 * @param strMap 页面提交信息
	 * 		business	: 业务ID
	 * 		dbtype		: 数据库类型
	 * @return
	 * 		Map[
	 * 			table		: 表格英文名
	 * 			table_ZH	: 表格中文名
	 * 			field		: 字段英文名
	 * 			field_ZH	: 字段中文名
	 * 			show		: 显示类型代码
	 * 			show_ZH		: 显示类型中文名
	 * 			title		: 提示信息
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> businessTableFieldsSingle(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "business", "DBTYPE")){
			return result;
		}
		String sql = "SELECT T1.ZGA18001,T1.ZGA18003,T1.ZGA18008,T2.ZGA19001,T2.ZGA19002,NVL(T2.ZGA19008,0),T3.A19001,T4.A18003 FROM ZGA018 T1 RIGHT JOIN ZGA019 T2 ON T1.ZGA18001=T2.ZGA18001 LEFT JOIN A19param2 T3 ON T3.A19002=T2.ZGA19018 LEFT JOIN A18param2 T4 ON T4.A18001=T3.A18001 WHERE T1.ZGA18002='0' AND T1.ZGA18005=param1 ORDER BY T1.ZGA18006,T2.ZGA19012"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "business")))
			.replaceAll("param2", MapUtil.compareKeyValue(strMap, "DBTYPE", "1")?"":"_SYS");
		List<List> tableFields = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tableFields)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<tableFields.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("table", tableFields.get(i).get(0));
			singleInfo.put("table_ZH", tableFields.get(i).get(1)+"("+(tableFields.get(i).get(2).equals("0")?"副":"主")+"表)");
			singleInfo.put("field", tableFields.get(i).get(4));
			singleInfo.put("field_ZH", tableFields.get(i).get(3));
			String showType = (String) tableFields.get(i).get(5);
			singleInfo.put("show", StringUtil.str2Int(showType));
			singleInfo.put("show_ZH", this.transShow(showType));
			if(!ObjectUtil.objIsNull(tableFields.get(i).get(7), tableFields.get(i).get(6))){
				singleInfo.put("title", tableFields.get(i).get(7)+":"+tableFields.get(i).get(6));
			}
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 获得业务表格中的字段信息
	 * @param strMap 页面提交的参数
	 * 		business:业务ID
	 * @return
	 * 		Map[
	 * 			table		: 英文表名, 
	 * 			table_ZH	: 中文表名, 
	 * 			field		: 字段中文名, 
	 * 			field_ZH	: 字段中文名, 
	 * 			show		: 字段显示类型代码, 
	 * 			show_ZH		: 字段显示类型中文名
	 *		]
	 * @throws Exception
	 */
	private Map<String, Object> tableFields(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "business")){
			return result;
		}
		String sql = "SELECT T1.ZGA18001,T1.ZGA18003,T1.ZGA18002,T1.ZGA18008,T2.ZGA19001,T2.ZGA19002,NVL(T2.ZGA19008,0) FROM ZGA018 T1 RIGHT JOIN ZGA019 T2 ON T1.ZGA18001=T2.ZGA18001 WHERE T1.ZGA18005='param1'OR T1.ZGA18005 LIKE 'param1,%' OR T1.ZGA18005 LIKE '%,param1' ORDER BY T1.ZGA18006,T2.ZGA19012"
			.replaceAll("param1", MapUtil.get(strMap, "business")+"");
		List<List> tableFields = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tableFields)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<tableFields.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("table", tableFields.get(i).get(0));
			singleInfo.put("table_ZH", tableFields.get(i).get(1)+"("+(tableFields.get(i).get(2).equals("0")?"单":"多")+"记录 "+(tableFields.get(i).get(3).equals("0")?"副":"主")+"表)");
			singleInfo.put("field", tableFields.get(i).get(5));
			singleInfo.put("field_ZH", tableFields.get(i).get(4));
			String showType = (String) tableFields.get(i).get(6);
			singleInfo.put("show", StringUtil.str2Int(showType));
			singleInfo.put("show_ZH", this.transShow(showType));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 报表排序配置字段
	 * 		获得报表已排序的字段信息
	 * @param strMap 页面提交信息
	 * 		config	: tab_045表ID
	 * @return
	 * 		Map[
	 * 			id			: 主键ID
	 * 			table		: 英文表名
	 * 			table_ZH	: 中文表名
	 * 			field		: 字段英文名
	 * 			field_ZH	: 字段中文名
	 * 			sort		: 排序类型
	 * 			before		: 上一个节点ID
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> sortConfigFields(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "config")){
			return result;
		}
		String sql = "SELECT T1.ZGA18001,T1.ZGA18003,T1.ZGA18002,T1.ZGA18008,T2.ZGA19001,T2.ZGA19002,T0.TAB_050_003,T0.ID FROM TAB_4011 T0 RIGHT JOIN ZGA018 T1 ON T1.ZGA18001=T0.TAB_050_001 RIGHT JOIN ZGA019 T2 ON T1.ZGA18001=T2.ZGA18001 AND T0.TAB_050_002=T2.ZGA19002 WHERE T0.TAB_045_ID=param1 ORDER BY T1.ZGA18006,T0.TAB_050_004"
			.replaceAll("param1", MapUtil.get(strMap, "config")+"");
		List<List> tableFields = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tableFields)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<tableFields.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("id", tableFields.get(i).get(7));
			singleInfo.put("table", tableFields.get(i).get(0));
			singleInfo.put("table_ZH", tableFields.get(i).get(1)+"("+(tableFields.get(i).get(2).equals("0")?"单":"多")+"记录 "+(tableFields.get(i).get(3).equals("0")?"副":"主")+"表)");
			singleInfo.put("field", tableFields.get(i).get(5));
			singleInfo.put("field_ZH", tableFields.get(i).get(4));
			singleInfo.put("sort", tableFields.get(i).get(6));
			singleInfo.put("target", true);
			singleInfo.put("before", i==0?"0":tableFields.get(i-1).get(7));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 报表配置中已使用的业务表和字段
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> sortTableFields(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "config")){
			return result;
		}
		String sql = "SELECT T1.ZGA18001,T1.ZGA18003,T1.ZGA18002,T1.ZGA18008,T2.ZGA19001,T2.ZGA19002 FROM (SELECT TAB_046_001 FROM TAB_4007 WHERE TAB_045_ID=param1 GROUP BY TAB_046_001) T0 LEFT JOIN ZGA018 T1 ON T1.ZGA18001=T0.TAB_046_001 LEFT JOIN ZGA019 T2 ON T1.ZGA18001=T2.ZGA18001 ORDER BY T1.ZGA18006,T2.ZGA19012"
			.replaceAll("param1", MapUtil.get(strMap, "config")+"");
		List<List> tableFields = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tableFields)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<tableFields.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("table", tableFields.get(i).get(0));
			singleInfo.put("table_ZH", tableFields.get(i).get(1)+"("+(tableFields.get(i).get(2).equals("0")?"单":"多")+"记录 "+(tableFields.get(i).get(3).equals("0")?"副":"主")+"表)");
			singleInfo.put("field", tableFields.get(i).get(5));
			singleInfo.put("field_ZH", tableFields.get(i).get(4));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 报表转换格式配置 待选表格字段
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> formatSelectFields(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "business", "table")){
			return result;
		}
		String sql = "SELECT T1.ZGA18001,T1.ZGA18003,T1.ZGA18002,T1.ZGA18008,T2.ZGA19001,T2.ZGA19002,NVL(T2.ZGA19008,0) FROM ZGA018 T1 RIGHT JOIN ZGA019 T2 ON T1.ZGA18001=T2.ZGA18001 WHERE T1.ZGA18001='param2' AND (T1.ZGA18005='param1' OR T1.ZGA18005 LIKE 'param1,%' OR T1.ZGA18005 LIKE '%,param1') ORDER BY T1.ZGA18006,T2.ZGA19012"
			.replaceAll("param1", MapUtil.get(strMap, "business")+"").replaceAll("param2", MapUtil.get(strMap, "TABLE")+"");
		List<List> tableFields = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tableFields)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<tableFields.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("table", tableFields.get(i).get(0));
			singleInfo.put("table_ZH", tableFields.get(i).get(1)+"("+(tableFields.get(i).get(2).equals("0")?"单":"多")+"记录 "+(tableFields.get(i).get(3).equals("0")?"副":"主")+"表)");
			singleInfo.put("field", tableFields.get(i).get(5));
			singleInfo.put("field_ZH", tableFields.get(i).get(4));
			String showType = (String) tableFields.get(i).get(6);
			singleInfo.put("show", showType);
			singleInfo.put("show_ZH", this.transShow(showType));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 报表配置
	 * 		已配置转换字段详细信息
	 * @param strMap 页面提交参数信息
	 * 		tab045 Tab_045表ID
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> configFields(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "tab045")){
			return result;
		}
		String sql = "SELECT T1.ID,T2.ZGA18003,T3.ZGA19001,T3.ZGA19008,T5.TAB_047_ID,T4.TAB_047_001,T1.TAB_046_006,T1.TAB_046_005,T2.ZGA18001,T3.ZGA19002,T1.TAB_046_003 FROM TAB_4007 T1 LEFT JOIN ZGA018 T2 ON T2.ZGA18001=T1.TAB_046_001 LEFT JOIN ZGA019 T3 ON T2.ZGA18001=T3.ZGA18001 AND T3.ZGA19002=T1.TAB_046_002 LEFT JOIN TAB_4010 T5 ON T5.TAB_046_ID=T1.ID AND T5.TAB_046_ID_01=T1.ID LEFT JOIN TAB_4008 T4 ON T4.ID=T5.TAB_047_ID WHERE TAB_045_ID=param1 AND TAB_046_007=0 ORDER BY TAB_046_001,TAB_046_003"
			.replaceAll("param1", StringUtil.strAddQuote(strMap.get("TAB045")));
		List<List> formatsList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(formatsList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<formatsList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("id", formatsList.get(i).get(0));
			singleInfo.put("table", formatsList.get(i).get(8));
			singleInfo.put("table_ZH", formatsList.get(i).get(1));
			singleInfo.put("field_ZH", formatsList.get(i).get(2));
			singleInfo.put("field", formatsList.get(i).get(9));
			singleInfo.put("show", formatsList.get(i).get(3));
			singleInfo.put("show_ZH", transShow(formatsList.get(i).get(3)));
			singleInfo.put("odata", formatsList.get(i).get(6));
			singleInfo.put("transformat", HtmlUtils.htmlUnescape(formatsList.get(i).get(7)+""));
			singleInfo.put("target", true);
			singleInfo.put("before", i==0?"0":formatsList.get(i-1).get(0));
			singleInfo.put("sort", formatsList.get(i).get(10));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 报表配置
	 * 		字段详细转换信息
	 * @param strMap 页面提交查询信息
	 * 		tab046 Tab_046表ID
	 * @return
	 * 		字段详细转换信息
	 * @throws Exception
	 */
	private Map<String, Object> formatFields(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "tab046")){
			return result;
		}
		String sql = "SELECT T1.ID,T2.ZGA18003,T3.ZGA19001,T3.ZGA19008,T5.TAB_047_ID,T4.TAB_047_001,T1.TAB_046_005,T6.TAB_048_001,T6.TAB_048_004,T5.TAB_049_001,T5.ID,T3.ZGA19002 FROM TAB_4010 T5 LEFT JOIN TAB_4007 T1 ON T5.TAB_046_ID_01=T1.ID LEFT JOIN ZGA018 T2 ON T2.ZGA18001=T1.TAB_046_001 LEFT JOIN ZGA019 T3 ON T2.ZGA18001=T3.ZGA18001 AND T3.ZGA19002=T1.TAB_046_002 LEFT JOIN TAB_4008 T4 ON T4.ID=T5.TAB_047_ID LEFT JOIN TAB_4009 T6 ON T6.ID=T5.TAB_048_ID WHERE T5.TAB_046_ID=param1 ORDER BY TAB_046_003"
			.replaceAll("param1", StringUtil.strAddQuote(strMap.get("TAB046")));
		List<List> formatsList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(formatsList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<formatsList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("id", formatsList.get(i).get(0));
			singleInfo.put("table_ZH", formatsList.get(i).get(1));
			singleInfo.put("field_ZH", formatsList.get(i).get(2));
			singleInfo.put("show", formatsList.get(i).get(3));
			singleInfo.put("show_ZH", transShow(formatsList.get(i).get(3)));
			singleInfo.put("transformat", HtmlUtils.htmlUnescape(formatsList.get(i).get(6)+""));
			singleInfo.put("target", true);
			singleInfo.put("before", i==0?"0":formatsList.get(i-1).get(0));
			singleInfo.put("format_ZH", formatsList.get(i).get(7));
			singleInfo.put("format_view", formatsList.get(i).get(8));
			singleInfo.put("formatflag", formatsList.get(i).get(9));
			singleInfo.put("formatid", formatsList.get(i).get(10));
			singleInfo.put("field", formatsList.get(i).get(11));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 转换字段类型代码
	 * @param type 字段类型代码
	 * @return
	 * 		返回字段类型中文名
	 */
	private String transShow(Object type){
		String showType = "";
		switch(ObjectUtil.obj2Int(type)){
		case 2:
			showType = "代码类型";
			break;
		case 3:
			showType = "日期类型";
			break;
		case 1:
		default:
			showType = "字符类型";
		}
		return showType;
	}
	
	/**
	 * 获得操作选项
	 * @return
	 * 		Map[url:连接URL, operation:操作名, isoracle:是否要开启数据库, state:连接状态, function:调用方法名]
	 * @throws Exception
	 */
	private Map<String, Object> operation() throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT S001,S002,S004,S005 FROM SYSTAB203 WHERE S003='1'";
		List<List> operationList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(operationList)){
			return result;
		}
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<operationList.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("url", operationList.get(i).get(1));
			singleInfo.put("operation", operationList.get(i).get(0));
			singleInfo.put("isoracle", operationList.get(i).get(2));
			singleInfo.put("state", "0");
			singleInfo.put("function", operationList.get(i).get(3));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 获得数据库IP列表
	 * @return
	 * 		Map[id:主键ID, IP:数据库IP, type:数据库类型(1=机关 0=基层)]
	 * @throws Exception
	 */
	private Map<String, Object> getIPsList() throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		String sql = "SELECT ID,S001,S002,S003 FROM SYSTAB201";
		List<List> ipLists = this.dbManager.executeQueryList(sql);
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		for(int i=0; i<ipLists.size(); i++){
			Map<Object, Object> singleInfo = new HashMap<Object, Object>();
			singleInfo.put("id", ipLists.get(i).get(0));
			singleInfo.put("IP", ipLists.get(i).get(1));
			singleInfo.put("type", ipLists.get(i).get(2));
			singleInfo.put("dbuserName", ipLists.get(i).get(3));
			recordsMap.add(singleInfo);
		}
		result.put("list", recordsMap);
		return result;
	}
	
	/**
	 * 获得用户状态信息
	 * @param strMap 页面提交信息
	 * 		author 用户ID
	 * @return
	 * 		连接状态信息
	 * @throws Exception
	 */
	private Map<String, Object> connStatus(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "author")){
			return result;
		}
		String sql = "SELECT S2.S001,S1.S003,S2.S002,S2.ID,S2.S003,S2.S004 FROM SYSTAB202 S1,SYSTAB201 S2 WHERE S1.S002=S2.ID AND S1.S001='param1'"
			.replaceAll("param1", MapUtil.get(strMap, "author")+"");
		List<List> queryResult = this.dbManager.executeQueryList(sql);
		List<Map<Object, Object>> recordsMap = new ArrayList<Map<Object, Object>>();
		Map<Object, Object> singleInfo1 = new HashMap<Object, Object>();
		singleInfo1.put("type", "数据库地址");
		singleInfo1.put("info", ListUtil.listIsNull(queryResult)?"":queryResult.get(0).get(0));
		recordsMap.add(singleInfo1);
		Map<Object, Object> singleInfo2 = new HashMap<Object, Object>();
		singleInfo2.put("type", "数据库类型");
		singleInfo2.put("info", ListUtil.listIsNull(queryResult)?"":(ObjectUtil.arrIsNULL(queryResult.get(0).get(2))?"":(StringUtil.compare2Obj(queryResult.get(0).get(2), "1")?"机关":"基层")));
		recordsMap.add(singleInfo2);
		
		/*************20140514修改*****************/
		Map<Object, Object> singleInfo3 = new HashMap<Object, Object>();
		singleInfo2.put("type", "数据库用户名");
		singleInfo2.put("info", ListUtil.listIsNull(queryResult)?"":queryResult.get(0).get(4));
		recordsMap.add(singleInfo3);
		
		Map<Object, Object> singleInfo4 = new HashMap<Object, Object>();
		singleInfo3.put("type", "连接时间");
		singleInfo3.put("info", ListUtil.listIsNull(queryResult)?"":queryResult.get(0).get(1));
		recordsMap.add(singleInfo4);
		Map<Object, Object> singleInfo5 = new HashMap<Object, Object>();
		singleInfo4.put("type", "连接状态");
		singleInfo4.put("info", ListUtil.listIsNull(queryResult)||!MapUtil.checkMapParamsNotNULL(strMap, "IP")?"<span style='color:red;'>未连接</div>":"<span style='color:green;'>已连接</div>");
		recordsMap.add(singleInfo5);
		
		result.put("list", recordsMap);
		
		return result;
	}
}
