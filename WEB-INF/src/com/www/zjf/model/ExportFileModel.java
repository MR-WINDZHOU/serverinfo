package com.www.zjf.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.util.HtmlUtils;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.StringUtil;

public class ExportFileModel {
	
	private DBManager dbManager = null;
	private Map<String, String> existMap = new Hashtable<String, String>();

	public ExportFileModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}
	
	/**
	 * 导出文档
	 * @param strMap 页面提交参数
	 * 			type	： 导出类型
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容
	 * 				title		: 导出文件名
	 * 			]
	 * @throws Exception
	 */
	public Map<String, Object> export(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			result.put("content", "参数错误！");
			return result;
		}
		switch(StringUtil.str2Int(strMap.get("TYPE"))){
		case 1://模板组导出SQL
			result = this.exportTempletConfig(strMap);
			break;
		case 2://导出业务下的所有模板信息
			result = this.exportBusinessTempletsConfig(strMap);
			if(!MapUtil.compareKeyValue(result, "result", "1")){//是否执行成功
				//查询业务名
				String sql = "SELECT WF05002 FROM WF05 WHERE WF05001=param1"
					.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "business")));
				List<List> businessName = this.dbManager.executeQueryList(sql);
				if(ListUtil.listIsNull(businessName)){
					return result;
				}else{
					String fileDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					result.put("title", (MapUtil.compareKeyValue(strMap, "DBTYPE", "1")?"基层":"机关")+MapUtil.get(strMap, "business")+""+businessName.get(0).get(0)+"脚本V1.0-"+MapUtil.get(strMap, "showname")+"-"+fileDate+".sql");
				}
			}
			break;
		case 3:
			result = this.exportNodeTempletsConfig(strMap);
			String sql = "SELECT WF05002 FROM WF05 WHERE WF05001=param1"
				.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "business")));
			List<List> businessName = this.dbManager.executeQueryList(sql);
			if(ListUtil.listIsNull(businessName)){
				return result;
			}else{
				if(!MapUtil.compareKeyValue(result, "result", "1")){
					String fileDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					result.put("title", MapUtil.get(strMap, "business")+""+businessName.get(0).get(0)+"-"+MapUtil.get(strMap, "node")+"环节脚本V1.0-"+MapUtil.get(strMap, "showname")+"-"+fileDate+".sql");
				}
			}
			break;
		}
		return result;
	}
	
	/**
	 * 导出本地数据库内容到文件
	 * @param strMap 页面提交参数
	 * 			type	： 导出类型
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容
	 * 				title		: 导出文件名
	 * 			]
	 * @throws Exception
	 */
	public Map<String, Object> sqliteExport(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			result.put("content", "参数错误！");
			return result;
		}
		switch(StringUtil.str2Int(strMap.get("TYPE"))){
		case 1://导出项目修改路径到文件
			result = this.exportProjectUrls(strMap);
			break;
		}
		return result;
	}
	
	/**
	 * 导出项目修改路径
	 * @param strMap 页面提交参数
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容 
	 * 				title		: 导出文件名
	 * 			]
	 * @throws Exception
	 */
	private Map<String, Object> exportProjectUrls(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "records")){//判断是否选中导出的路径
			result.put("content", "参数错误！");
			return result;
		}
		String author = strMap.get("AUTHOR");
		//查询路径信息
		String sql = "SELECT S2.S001,S1.S006,S1.S001,S3.S004,S1.S004 FROM SYSTAB205 S1 LEFT JOIN SYSTAB204 S2 ON S1.S003=S2.ID LEFT JOIN SYSTAB200 S3 ON S3.ID=S1.S002 WHERE S1.ID IN (param1) ORDER BY S2.S001,S1.ID"
			.replaceAll("param1", StringUtil.str2QuoteStr(strMap.get("RECORDS")));
		List<List> editUrls = this.dbManager.executeQueryList(sql);
		
		String projectName = "";
		StringBuffer content = new StringBuffer();
		for(int i=0; i<editUrls.size(); i++){
			if(!editUrls.get(i).get(0).equals(projectName)){
				projectName = editUrls.get(i).get(0)+"";
				content.append("\r\n"+editUrls.get(i).get(0)+"\r\n");
			}
			content.append("\t"+editUrls.get(i).get(1)+"\t"+editUrls.get(i).get(2)+"\t("+editUrls.get(i).get(3)+" "+editUrls.get(i).get(4).toString().substring(0, 10)+"修改)\r\n");
		}
		result.put("content", content);
		sql = "SELECT S004 FROM SYSTAB200 WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(author));
		List<List> displayName = this.dbManager.executeQueryList(sql);
		result.put("title", "项目修改路径V1.0-"+displayName.get(0).get(0)+"-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return result;
	}
	
	/**
	 * 导出业务下的所有模板信息
	 * @param strMap 页面提交参数
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容 
	 * 			]
	 * @throws Exception
	 */
	private Map<String, Object> exportBusinessTempletsConfig(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "business")){//判断业务ID是否为空
			result.put("result", "0");
			result.put("content", "参数错误！");
			return result;
		}
		//查询业务下的所有环节
		String sql = "SELECT T2.WF07001 FROM WF06 T1 RIGHT JOIN WF07 T2 ON T1.WF06001=T2.WF07005 WHERE T1.WF06005=param1 ORDER BY T2.WF07001"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "business")));
		List<List> nodesList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(nodesList)){//判断业务下是否存在环节
			result.put("result", "0");
			result.put("content", "查询业务环节数为0！");
			return result;
		}
		StringBuilder content = new StringBuilder();
		for(int i=0; i<nodesList.size(); i++){
			//调用其他方法的参数Map
			Map<String, String> birtParams = new Hashtable<String, String>();
			birtParams.put("business", MapUtil.get(strMap, "business")+"");
			birtParams.put("node", nodesList.get(i).get(0)+"");
			//获得单个环节下的模板信息
			Map<String, Object> birtContent = this.exportNodeTempletsConfig(birtParams);
			if(!MapUtil.compareKeyValue(birtContent, "result", "1")){//判断是否执行成功
				content.append(birtContent.get("content"));
				content.append("\n");
			}
		}
		result.put("result", "1");
		result.put("content", content);
		return result;
	}
	
	/**
	 * 获得环节下的所有模板信息
	 * @param strMap 页面提交参数
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容 
	 * 			]
	 * @throws Exception
	 */
	private Map<String, Object> exportNodeTempletsConfig(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "node", "business")){//判断业务ID，环节ID是否为空
			result.put("result", "0");
			result.put("content", "参数错误！");
			return result;
		}
		StringBuilder content = new StringBuilder();
		//查询环节下的所有模板信息
		String sql = "SELECT T3.ID FROM TAB_4002 T1 RIGHT JOIN TAB_4003 T2 ON T1.TAB_009_001 = T2.ID RIGHT JOIN TAB_4006 T3 ON T2.ID=T3.TAB_010_ID AND T3.TAB_045_001=param2 AND T3.TAB_045_002=param1 WHERE T1.TAB_009_002 =param1 ORDER BY T2.ID"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "node")))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "business")));
		List<List> birtsList = this.dbManager.executeQueryList(sql);
		for(int j=0; j<birtsList.size(); j++){
			//获得模板信息时，调用方法时的参数Map
			Map<String, String> birtParams = new Hashtable<String, String>();
			birtParams.put("configid", birtsList.get(j).get(0)+"");
			//调用模板方法，获得单个模板的信息
			Map<String, Object> birtContent = this.exportTempletConfig(birtParams);
			if(!MapUtil.compareKeyValue(birtContent, "result", "1")){
				content.append(birtContent.get("content"));
				content.append("\n");
			}
		}
		result.put("result", "1");
		result.put("content", content);
		return result;
	}
	
	/**
	 * 导出模板配置SQL
	 * @param strMap 页面提交信息
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容 
	 * 			]
	 * @throws Exception
	 */
	private Map<String, Object> exportTempletConfig(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "configid")){//判断保镖配置主键ID 是否为空
			result.put("result", "0");
			result.put("content", "参数错误！");
			return result;
		}
		//查询报表配置信息 模板ID，业务ID，环节ID，是否确定使用
		String sql = "SELECT TAB_010_ID,TAB_045_001,TAB_045_002,TAB_045_003,TAB_045_004,TAB_045_005,ID FROM TAB_4006 WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "configid")));
		List<List> tab045List = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tab045List)){//报表未配置
			result.put("result", "0");
			result.put("content", "报表未配置！");
			return result;
		}
		if(!ObjectUtil.compare2Obj(tab045List.get(0).get(5), "1")){//报表是否启用
			result.put("result", "0");
			result.put("content", "报表未确定使用，请先使用再导出！");
			return result;
		}
		//查询报表模板文件名
		sql = "SELECT T2.TAB_010_001 FROM TAB_4006 T1 RIGHT JOIN TAB_4003 T2 ON T2.ID=T1.TAB_010_ID WHERE T1.ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "configid")));
		List<List> tab010List = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tab010List)){//模板不存在
			result.put("result", "0");
			result.put("content", "模板不存在！");
			return result;
		}
		//拼接脚步SQL
		String sqlSplice = "";
		sqlSplice = "declare\n"
			+"temp_id1 number DEFAULT -1;\n"
			+"temp_id2 number DEFAULT -1;\n"
			+"temp_id3 number DEFAULT -1;\n"
			+"temp_id4 number DEFAULT -1;\n"
			+"begin\n"
			+"SELECT ID INTO TEMP_ID2 FROM TAB_4003 WHERE TAB_010_001=param1;\n".replaceAll("param1", StringUtil.strAddQuote(tab010List.get(0).get(0)))
			+"DELETE FROM TAB_4006 WHERE TAB_010_ID=TEMP_ID2 AND TAB_045_001=param1 AND TAB_045_002=param2;\n".replaceAll("param1", StringUtil.strAddQuote(tab045List.get(0).get(1))).replaceAll("param2", StringUtil.strAddQuote(tab045List.get(0).get(2)))
			+"INSERT INTO TAB_4006(TAB_010_ID,TAB_045_001,TAB_045_002,TAB_045_003,TAB_045_004,TAB_045_005)VALUES(TEMP_ID2, param1, param2, param3, param4, param5) RETURN ID INTO TEMP_ID1;\n".replaceAll("param1", StringUtil.strAddQuote(tab045List.get(0).get(1)))
			.replaceAll("param2", StringUtil.strAddQuote(tab045List.get(0).get(2)))
			.replaceAll("param3", StringUtil.strAddQuote(tab045List.get(0).get(3)))
			.replaceAll("param4", StringUtil.strAddQuote(tab045List.get(0).get(4)))
			.replaceAll("param5", StringUtil.strAddQuote(tab045List.get(0).get(5)));
		//查询报表使用的字段信息
		sql = "SELECT ID, tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007 FROM TAB_4007 WHERE tab_045_id=param1".replaceAll("param1", StringUtil.strAddQuote(tab045List.get(0).get(6)));
		List<List> tab046List = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tab046List)){//报表是否存在配置的字段
			result.put("result", "0");
			result.put("content", "报表未配置字段，请先配置字段后再导出SQL脚本！");
			return result;
		}
		//将报表字段保存到Map中，这主要是下面使用，防止出现字段重复的想象
		Map<String, List> tab046Map = new Hashtable<String, List>();
		for(int i=0; i<tab046List.size(); i++){
			tab046Map.put(tab046List.get(i).get(0)+"", tab046List.get(i));
		}
		//查询birt配置字段中转换时，使用到的是自身的字段
		sql = "SELECT T2.TAB_046_ID FROM TAB_4007 T1 RIGHT JOIN TAB_4010 T2 ON T2.TAB_046_ID = T1.ID WHERE T1.TAB_045_ID=param1 GROUP BY T2.TAB_046_ID"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "configid")));
		List<List> tab049FilterList = this.dbManager.executeQueryList(sql);
		for(int i=0; i<tab049FilterList.size(); i++){
			List singleInfo = tab046Map.get(tab049FilterList.get(i).get(0)+"");
			tab046Map.remove(tab049FilterList.get(i).get(0)+"");//移出字段
			//添加字段到SQL
			sqlSplice+= "INSERT INTO TAB_4007(tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007,tab_045_id) VALUES(param1,param2,param3, param4, param5,param6, param7,TEMP_ID1) RETURN ID INTO TEMP_ID3;\n"
				.replaceAll("param1", StringUtil.strAddQuote(singleInfo.get(1)))
				.replaceAll("param2", StringUtil.strAddQuote(singleInfo.get(2)))
				.replaceAll("param3", StringUtil.strAddQuote(singleInfo.get(3)))
				.replaceAll("param4", StringUtil.strAddQuote(singleInfo.get(4)))
				.replaceAll("param5", HtmlUtils.htmlUnescape(StringUtil.strAddQuote(singleInfo.get(5))))
				.replaceAll("param6", StringUtil.strAddQuote(singleInfo.get(6)))
				.replaceAll("param7", StringUtil.strAddQuote(singleInfo.get(7)));
			//查询关联的字段
			sql = "SELECT tab_046_id_01, tab_047_id, tab_048_id, tab_049_001 FROM TAB_4010 WHERE tab_046_id=param1"
				.replaceAll("param1", StringUtil.strAddQuote(tab049FilterList.get(i).get(0)));
			List<List> subnodeList = this.dbManager.executeQueryList(sql);
			for(int j=0; j<subnodeList.size(); j++){
				List subSingleInfo = tab046Map.get(subnodeList.get(j).get(0)+"");
				tab046Map.remove(subnodeList.get(j).get(0)+"");
				if(ObjectUtil.arrIsNULL(subSingleInfo)){
					//添加转换格式
					sqlSplice+= "INSERT INTO TAB_4010(TAB_046_ID,TAB_046_ID_01,TAB_047_ID,TAB_048_ID,tab_049_001) VALUES(TEMP_ID3, TEMP_ID3, param1, param2, param3);\n"
						.replaceAll("param1", StringUtil.strAddQuote(subnodeList.get(j).get(1)))
						.replaceAll("param2", StringUtil.strAddQuote(subnodeList.get(j).get(2)))
						.replaceAll("param3", StringUtil.strAddQuote(subnodeList.get(j).get(3)));
				}else{
					sqlSplice+= "INSERT INTO TAB_4007(tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007,tab_045_id) VALUES(param1,param2,param3, param4, param5,param6, param7,TEMP_ID1) RETURN ID INTO TEMP_ID4;\n"
						.replaceAll("param1", StringUtil.strAddQuote(subSingleInfo.get(1)))
						.replaceAll("param2", StringUtil.strAddQuote(subSingleInfo.get(2)))
						.replaceAll("param3", StringUtil.strAddQuote(subSingleInfo.get(3)))
						.replaceAll("param4", StringUtil.strAddQuote(subSingleInfo.get(4)))
						.replaceAll("param5", HtmlUtils.htmlUnescape(StringUtil.strAddQuote(subSingleInfo.get(5))))
						.replaceAll("param6", StringUtil.strAddQuote(subSingleInfo.get(6)))
						.replaceAll("param7", StringUtil.strAddQuote(subSingleInfo.get(7)))
						+ "INSERT INTO TAB_4010(TAB_046_ID,TAB_046_ID_01,TAB_047_ID,TAB_048_ID,tab_049_001) VALUES(TEMP_ID3, TEMP_ID4, param1, param2, param3);\n"
						.replaceAll("param1", StringUtil.strAddQuote(subnodeList.get(j).get(1)))
						.replaceAll("param2", StringUtil.strAddQuote(subnodeList.get(j).get(2)))
						.replaceAll("param3", StringUtil.strAddQuote(subnodeList.get(j).get(3)));
				}
			}
		}
		//保存其他未使用的字段
		Set<String> keySet = tab046Map.keySet();
		for(String s: keySet){
			List singleInfo = tab046Map.get(s);
			sqlSplice += "INSERT INTO TAB_4007(tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007,tab_045_id) VALUES(param1,param2,param3, param4, param5,param6, param7,TEMP_ID1);\n"
				.replaceAll("param1", StringUtil.strAddQuote(singleInfo.get(1)))
				.replaceAll("param2", StringUtil.strAddQuote(singleInfo.get(2)))
				.replaceAll("param3", StringUtil.strAddQuote(singleInfo.get(3)))
				.replaceAll("param4", StringUtil.strAddQuote(singleInfo.get(4)))
				.replaceAll("param5", HtmlUtils.htmlUnescape(StringUtil.strAddQuote(singleInfo.get(5))))
				.replaceAll("param6", StringUtil.strAddQuote(singleInfo.get(6)))
				.replaceAll("param7", StringUtil.strAddQuote(singleInfo.get(7)));
		}
		//查询排序方法
		sql = "SELECT tab_050_001, tab_050_002, tab_050_003, tab_050_004 FROM TAB_4011 WHERE tab_045_id=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "configid")));
		List<List> tab050List = this.dbManager.executeQueryList(sql);
		for(int i=0;i<tab050List.size(); i++){
			sqlSplice+= "INSERT INTO TAB_4011(tab_050_001, tab_050_002, tab_050_003, tab_050_004, TAB_045_ID) VALUES(param1, param2, param3, param4, temp_id1);\n"
				.replaceAll("param1", StringUtil.strAddQuote(tab050List.get(i).get(0)))
				.replaceAll("param2", StringUtil.strAddQuote(tab050List.get(i).get(1)))
				.replaceAll("param3", StringUtil.strAddQuote(tab050List.get(i).get(2)))
				.replaceAll("param4", StringUtil.strAddQuote(tab050List.get(i).get(3)));
		}
		//查询过滤条件
		sql = "SELECT tab_051_001, tab_051_002 FROM TAB_4012 WHERE tab_045_id=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "configid")));
		List<List> tab051List = this.dbManager.executeQueryList(sql);
		for(int i=0; i<tab051List.size(); i++){
			sqlSplice+= "INSERT INTO TAB_4012(tab_051_001, tab_051_002, TAB_045_ID) VALUES(param1, param2, temp_id1);\n"
				.replaceAll("param1", StringUtil.strAddQuote(tab051List.get(i).get(0)))
				.replaceAll("param2", StringUtil.strAddQuote(tab051List.get(i).get(1)));
		}
		sqlSplice	+="END;\n"
					+"/\n";
		result.put("result", "1");
		result.put("content", sqlSplice);
		return result;
	}
	
	/**
	 *  导出模板信息
	 * @param strMap 页面提交参数
	 * 		type	: 导出类型
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容
	 * 				title		: 导出文件名
	 * 			]
	 * @throws Exception
	 */
	public Map<String, Object> exportTemplet(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type", "id")){
			result.put("content", "参数错误！");
			return result;
		}
		String sql = "";
		String sqlSplice = "";
		String seachSql = "";
		switch(StringUtil.str2Int(strMap.get("TYPE"))){
		case 1://模板组导出SQL
			sql = "declare\n"
				+"temp_num number;\n"
				+"temp_id number DEFAULT -1;\n"
				+"begin\n";
			sql += this.templetGroupExport(strMap.get("ID"));
			sql += "END;";
			result.put("content", sql);
			//查询模板组名
			seachSql = "SELECT TAB_008_001 FROM TAB_4001 WHERE ID=param1"
				.replaceAll("param1", strMap.get("ID"));
			List<List> groupName = this.dbManager.executeQueryList(seachSql);
			if(!ListUtil.listIsNull(groupName)){
				result.put("title", groupName.get(0).get(0)+"模板组(模板管理)SQL脚本V1.0-");
			}
			break;
		case 2://模板导出SQL
			sqlSplice = "declare\n"
				+"temp_num number;\n"
				+"temp_id number DEFAULT -1;\n"
				+"temp_id1 number;\n"
				+"temp_id2 number;\n"
				+"begin\n"
				+this.templetExportSql(strMap.get("ID"), true)
				+"END;\n";
			result.put("content", sqlSplice);
			//查询模板名
			seachSql = "SELECT TAB_010_001 FROM TAB_4003 WHERE ID=param1"
				.replaceAll("param1", strMap.get("ID"));
			List<List> templetName = this.dbManager.executeQueryList(seachSql);
			if(!ListUtil.listIsNull(templetName)){
				result.put("title", templetName.get(0).get(0)+"模板SQL脚本(模板管理)V1.0-");
			}
			break;
		case 3://单个模板导出
			sqlSplice = "declare\n"
				+"temp_num number;\n"
				+"temp_id number DEFAULT -1;\n"
				+"temp_id1 number;\n"
				+"temp_id2 number;\n"
				+"begin\n"
				+this.nodeTemplet(strMap.get("ID"), strMap.get("NODEID"), true)
				+"END;";
			result.put("content", sqlSplice);
			break;
		case 4://环节模板导出
			sqlSplice = "declare\n"
				+"temp_num number;\n"
				+"temp_id number DEFAULT -1;\n"
				+"temp_id1 number;\n"
				+"temp_id2 number;\n"
				+"begin\n"
				+this.nodeTemplets(strMap.get("ID"),true)
				+"END;";
			result.put("content", sqlSplice);
			//查询业务名
			seachSql = "SELECT WF06.WF06001 FROM WF07 LEFT JOIN WF06 ON WF06.WF06001=WF07.WF07005 WHERE WF07.WF07001=param1"
				.replaceAll("param1", strMap.get("ID"));
			List<List> businessName = this.dbManager.executeQueryList(seachSql);
			if(!ListUtil.listIsNull(businessName)){
				result.put("title", businessName.get(0).get(0)+"业务 "+strMap.get("ID")+"环节下所有模板信息SQL脚本(模板管理)V1.0-");
			}
			break;
		case 5://业务模板导出
			sqlSplice = "declare\n"
				+"temp_num number;\n"
				+"temp_id number DEFAULT -1;\n"
				+"temp_id1 number;\n"
				+"temp_id2 number;\n"
				+"begin\n"
				+this.businessTemplets(strMap.get("ID"))
				+"END;";
			result.put("content", sqlSplice);
			result.put("title", strMap.get("ID")+"业务下所有模板信息SQL脚本(模板管理)V1.0-");
			break;
		case 6://分类下 模板导出
			sqlSplice = "declare\n"
				+"temp_num number;\n"
				+"temp_id number DEFAULT -1;\n"
				+"temp_id1 number;\n"
				+"temp_id2 number;\n"
				+"begin\n"
				+this.typeTempletes(strMap.get("ID"))
				+"END;";
			result.put("content", sqlSplice);
			break;
		}
		return result;
	}
	
	/**
	 * 删除模板操作
	 * @param strMap 页面提交参数
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 				content	: 文件输出内容 
	 * 			]
	 * @throws Exception
	 */
	public Map<String, Object> exportDelete(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type", "id")){//判断参数中type和id是否为空，如果为空，直接返回
			result.put("content", "参数错误！");
			result.put("result", "0");
			return result;
		}
		String sql = "";
		List<List> templetInfo = new ArrayList<List>();
		String sqlSplite = "";
		switch(StringUtil.str2Int(strMap.get("TYPE"))){
		case 1://模板删除
			//查询模板文件名
			sql = "SELECT TAB_010_001 FROM TAB_4003 WHERE ID=param1"
				.replaceAll("param1", StringUtil.strAddQuote(strMap.get("ID")));
			templetInfo = this.dbManager.executeQueryList(sql);
			if(ListUtil.listIsNull(templetInfo)){//无信息
				result.put("content", "参数错误！");
				result.put("result", "0");
				return result;
			}
			//删除模板表中的明白信息
			sql = "DELETE FROM TAB_4003 WHERE ID=param1"
				.replaceAll("param1", StringUtil.strAddQuote(strMap.get("ID")));
			this.dbManager.executeUpdateSQL(sql);
			//拼接删除脚步SQL
			sqlSplite = "DECLARE\n"
			+"begin\n"
			+"DELETE FROM TAB_4003 WHERE TAB_010_001=param1;\n".replaceAll("param1", StringUtil.strAddQuote(templetInfo.get(0).get(0)+""))
			+"end;\n";
			result.put("result", "1");
			result.put("content", sqlSplite);
			break;
		case 2://业务表删除
			//查询模板信息
			sql = "SELECT TAB_010_001 FROM TAB_4003 WHERE ID=param1"
				.replaceAll("param1", StringUtil.strAddQuote(strMap.get("ID")));
			templetInfo = this.dbManager.executeQueryList(sql);
			if(ListUtil.listIsNull(templetInfo)&&!MapUtil.checkMapParamsNotNULL(strMap, "nodeid")){//判断是否存在模板，并且环节ID不能为空
				result.put("content", "参数错误！");
				result.put("result", "0");
				return result;
			}
			//删除环节下该明白的信息
			sql = "DELETE FROM TAB_4002 WHERE TAB_009_001=param1 AND TAB_009_002=param2"
				.replaceAll("param1", StringUtil.strAddQuote(strMap.get("ID")))
				.replaceAll("param2", StringUtil.strAddQuote(strMap.get("NODEID")));
			this.dbManager.executeUpdateSQL(sql);
			//拼接环节下模板删除脚本
			sqlSplite = "DECLARE\n"
				+"TEMP_ID NUMBER;\n"
				+"begin\n"
				+"SELECT ID INTO TEMP_ID FROM TAB_4003 WHERE TAB_010_001=param1;\n".replaceAll("param1", StringUtil.strAddQuote(templetInfo.get(0).get(0)+""))
				+"DELETE FROM TAB_4002 WHERE TAB_009_001=TEMP_ID AND TAB_009_002=param1;\n".replaceAll("param1", StringUtil.strAddQuote(strMap.get("NODEID")))
				+"end;\n"
				+"/\n";
			result.put("result", "1");
			result.put("content", sqlSplite);
			break;
		case 3:break;
		}
		return result;
	}
	
	/**
	 * 环节单个模板导出
	 * @param templetId 模板ID
	 * @param nodeId 环节ID
	 * @param flag 模板中是否查询上级节点
	 * @return
	 * 	脚步SQL
	 * @throws Exception
	 */
	private String nodeTemplet(String templetId, String nodeId, boolean flag) throws Exception{
		if(StringUtil.arrIsNULL(templetId, nodeId)){
			return "";
		}
		String sqlSplice = this.templetExportSql(templetId, flag);
		sqlSplice += "Insert into TAB_4002 (TAB_009_001,TAB_009_002,TAB_009_003) values (TEMP_ID1,param1,'01-1月 -10 12.00.00.000000000 上午');\n";
		sqlSplice = sqlSplice.replaceAll("param1", nodeId);
		return sqlSplice;
	}
	
	/**
	 * 获得环节下的模板信息
	 * @param nodeId 环节ID
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	private String nodeTemplets(String nodeId, boolean flag) throws Exception{
		if(StringUtil.arrIsNULL(nodeId)){
			return "";
		}
		String sql = "SELECT TAB_009_001 FROM TAB_4002 WHERE TAB_009_002=param1"
			.replaceAll("param1", StringUtil.strAddQuote(nodeId));
		List<List> templetsList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(templetsList)){
			return "";
		}
		String sqlSplice = "";
		for(int i=0; i<templetsList.size(); i++){
			sqlSplice += this.nodeTemplet(templetsList.get(i).get(0)+"", nodeId, flag);
		}
		return sqlSplice;
	}
	
	/**
	 * 获得业务下的模板信息
	 * @param businessId 业务ID
	 * @return
	 * 	脚本SQL
	 * @throws Exception
	 */
	private String businessTemplets(String businessId) throws Exception{
		if(StringUtil.arrIsNULL(businessId)){//判断业务ID是否为空
			return "";
		}
		//获得业务 下的所有环节
		String sql = "SELECT T2.WF07001 FROM WF06 T1 RIGHT JOIN WF07 T2 ON T1.WF06001=T2.WF07005 WHERE T1.WF06005=param1 ORDER BY T2.WF07001"
			.replaceAll("param1", StringUtil.strAddQuote(businessId));
		List<List> nodesList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(nodesList)){//如果环节为空，返回空字符串
			return "";
		}
		String sqlSplice = "";
		//循环所有的环节，取出环节下的模板信息
		for(int i=0; i<nodesList.size(); i++){
			sqlSplice += this.nodeTemplets(nodesList.get(i).get(0)+"", i==0);
		}
		return sqlSplice;
	}
	
	/**
	 * 业务类型下模板的导出
	 * @param typeId 业务类型ID
	 * @return
	 * 	脚步SQL
	 * @throws Exception
	 */
	private String typeTempletes(String typeId) throws Exception{
		if(StringUtil.arrIsNULL(typeId)){//判断类型ID是否为空
			return "";
		}
		//查询业务类型下的所有业务
		String sql = "SELECT T3.WF05001 FROM ZGA001 T1 RIGHT JOIN ZGA002 T2 ON T1.ZGA001001=T2.ZGA001001 AND T2.ZGA002003=0 RIGHT JOIN WF05 T3 ON T3.WF05001=T2.ZGA002002 WHERE T1.ZGA001002 LIKE '%,param1' ORDER BY T3.WF05007"
			.replaceAll("param1", typeId);
		List<List> businesssList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(businesssList)){//如果不存在 返回空字符串
			return "";
		}
		String sqlSplice = "";
		//循环所有的业务，并获得业务下的所有模板信息
		for(int i=0; i<businesssList.size(); i++){
			sqlSplice += this.businessTemplets(businesssList.get(i).get(0)+"");
		}
		return sqlSplice;
	}
	
	/**
	 * 报表组导出
	 * @param id 模板组ID
	 * @return
	 * 		脚步SQL
	 * @throws Exception
	 */
	private String templetGroupExport(String id) throws Exception{
		if(StringUtil.arrIsNULL(id)||id.equals("-1")){
			return "";
		}
		String sql = "SELECT ID,TAB_008_001,TAB_008_002 FROM TAB_4001 WHERE ID=param1"
			.replaceAll("param1", id);
		List<List> groupInfo = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(groupInfo)){
			return "";
		}
		String sqlGroup = this.templetGroupExport(StringUtil.obj2Str(groupInfo.get(0).get(2)));
		sqlGroup += "select count(1) into temp_num from TAB_4001 where tab_008_001='param1' AND TAB_008_002=temp_id;\n"
			+"if temp_num=0 then \n"
			+"Insert into TAB_4001 (TAB_008_001,TAB_008_002) values ('param1',TEMP_ID) return id into temp_id;\n"
			+"else\n"
			+"SELECT ID INTO TEMP_ID FROM TAB_4001 WHERE tab_008_001='param1' AND TAB_008_002=temp_id;\n"
			+"end if;\n";
		sqlGroup = sqlGroup.replaceAll("param1", StringUtil.obj2Str(groupInfo.get(0).get(1)));
		return sqlGroup;
	}
	
	/**
	 * 导出模板信息
	 * @param tab010Id
	 * @param flag
	 * @return
	 * 		脚步SQL
	 * @throws Exception
	 */
	private String templetExportSql(String tab010Id, boolean flag) throws Exception{
		//查询Tab_008表ID
		String sql = "SELECT TAB_011_001 FROM TAB_4004 WHERE TAB_011_002=param1"
			.replaceAll("param1", StringUtil.strAddQuote(tab010Id));
		List<List> tab012ID = this.dbManager.executeQueryList(sql);
		if("".equals(tab012ID.get(0).get(0))){
			return "";
		}
		sql = "SELECT TAB_012_001 FROM TAB_4005 WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(tab012ID.get(0).get(0)+""));
		List<List> pathId = this.dbManager.executeQueryList(sql);
		String tab008Id = pathId.get(0).get(0)+"";
		tab008Id = tab008Id.substring(tab008Id.lastIndexOf(",")+1);
		sql = "SELECT TAB_010_001,TAB_010_002,ISSYNCHRONOUS,IS_EXCEL FROM TAB_4003 WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(tab010Id));
		List<List> templetInfo = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(templetInfo)){
			return "";
		}
		String sqlSplice = this.templetExport(tab008Id, flag);
		if(existMap.containsKey("templet-"+templetInfo.get(0).get(0))){
			sqlSplice += "SELECT ID INTO TEMP_ID1 FROM TAB_4003 WHERE TAB_010_001=param1;\n".replaceAll("param1", StringUtil.strAddQuote(templetInfo.get(0).get(0)+""));
		}else{
			sqlSplice += "SELECT COUNT(1) INTO TEMP_NUM FROM TAB_4003 WHERE TAB_010_001=param1;\n".replaceAll("param1", StringUtil.strAddQuote(templetInfo.get(0).get(0)+""))
				+"if temp_num=0 then \n"
					+ "Insert into TAB_4003 (TAB_010_001,TAB_010_002,TAB_010_003,ISSYNCHRONOUS,IS_EXCEL) values (param1,param2,'01-1月 -10 12.00.00.000000000 上午',param3,param4) return id into temp_id1;\n"
							.replaceAll(
									"param1",
									StringUtil.strAddQuote(templetInfo.get(0)
											.get(0) + ""))
							.replaceAll(
									"param2",
									HtmlUtils.htmlUnescape(StringUtil
											.strAddQuote(templetInfo.get(0)
													.get(1) + "")))
							.replaceAll(
									"param3",
									StringUtil.strAddQuote(templetInfo.get(0)
											.get(2) + ""))
							.replaceAll(
									"param4",
									StringUtil.strAddQuote(templetInfo.get(0)
											.get(3) + ""))
				+"Insert into TAB_4004 (TAB_011_001,TAB_011_002) values (temp_id2,temp_id1);\n"
				+"ELSE\n"
				+"SELECT ID INTO TEMP_ID1 FROM TAB_4003 WHERE TAB_010_001=param1;\n".replaceAll("param1", StringUtil.strAddQuote(templetInfo.get(0).get(0)+""))
				+"end if;\n";
			existMap.put("templet-"+templetInfo.get(0).get(0), "");
		}
		return sqlSplice;
	}
	
	/**
	 * 报表导出
	 * @param id 模板ID
	 * @param flag 脚步中是否查询上级节点
	 * @return
	 * 		脚步SQL
	 * @throws Exception
	 */
	private String templetExport(String id, boolean flag) throws Exception{
		if(StringUtil.arrIsNULL(id)||id.equals("-1")){
			return "";
		}
		//查询报表名称 ID 文件名
		String sql = "SELECT ID,TAB_008_001,TAB_008_002 FROM TAB_4001 WHERE ID=param1"
			.replaceAll("param1", id);
		List<List> groupInfo = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(groupInfo)){//报表不存在
			return "";
		}
		String sqlGroup = this.templetExport(StringUtil.obj2Str(groupInfo.get(0).get(2)), false);
		//判断报表是否已生成脚步
		if(existMap.containsKey(StringUtil.obj2Str(groupInfo.get(0).get(1)))){//报表已导出到SQL
			if(flag){
				sqlGroup += "SELECT ID INTO TEMP_ID FROM TAB_4001 WHERE tab_008_001='param1';\n".replaceAll("param1", StringUtil.obj2Str(groupInfo.get(0).get(1)))
					+"select ID into temp_id2 from TAB_4005 where tab_012_001 like '%,'||temp_id or tab_012_001=to_char(temp_id);\n";
			}
		}else{//报表未导出到SQL
			sqlGroup += "select count(1) into temp_num from TAB_4001 where tab_008_001='param1';\n".replaceAll("param1", StringUtil.obj2Str(groupInfo.get(0).get(1)))
				+"if temp_num=0 then \n"
				+"Insert into TAB_4001 (TAB_008_001,TAB_008_002) values ('param1',TEMP_ID) return id into temp_id;\n".replaceAll("param1", StringUtil.obj2Str(groupInfo.get(0).get(1)));
			if(flag){
				sqlGroup += "else\n"
				+"SELECT ID INTO TEMP_ID FROM TAB_4001 WHERE tab_008_001='param1';\n".replaceAll("param1", StringUtil.obj2Str(groupInfo.get(0).get(1)))
				+"end if;\n"
				+"select ID into temp_id2 from TAB_4005 where tab_012_001 like '%,'||temp_id or tab_012_001=to_char(temp_id);\n";
			}else{
				sqlGroup += "end if;\n";
			}
			existMap.put(StringUtil.obj2Str(groupInfo.get(0).get(1)), "");
		}
		return sqlGroup;
	}
	
}
