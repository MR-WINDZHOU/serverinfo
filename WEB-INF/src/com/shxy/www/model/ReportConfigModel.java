package com.shxy.www.model;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.StringUtil;

public class ReportConfigModel {
	private DBManager dbManager = null;

	public ReportConfigModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}
	
	/**
	 * 查询报表配置ID
	 * @param strMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> reportConfig(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "business", "node", "report")){//查询业务ID，环节ID，及报表名是否为空
			result.put("result", "0");
			return result;
		}
		//查询报表是否已配置
		String sql = "SELECT ID FROM TAB_4006 WHERE tab_010_id=param1 AND tab_045_001=param2 AND tab_045_002=param3"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "report")))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "business")))
			.replaceAll("param3", StringUtil.strAddQuote(MapUtil.get(strMap, "node")));
		List<List> configInfo = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(configInfo)){//如果未配置
			//向tab_045表添加配置信息
			sql = "INSERT INTO TAB_4006(TAB_010_ID,TAB_045_001,TAB_045_002) VALUES(param1, param2, param3)"
				.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "report")))
				.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "business")))
				.replaceAll("param3", StringUtil.strAddQuote(MapUtil.get(strMap, "node")));
			String record = this.dbManager.executeInsertSQL(sql);
			result.put("record", record);
		}else{
			result.put("record", configInfo.get(0).get(0));
		}
		result.put("result", "1");
		return result;
	}
	
	/**
	 * 调整字段顺序
	 * @param strMap
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> changeOrders(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "before")){
			result.put("result", "0");
			return result;
		}
		Map<String, Object> callParams = new Hashtable<String, Object>();
		callParams.put("CALLNAME", "{CALL P_REP_ORDER(?, ?, ?)}");
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
	 * 向报表模板中插入字段
	 * @param strMap
	 * @return
	 * 		Map[
	 * 			result	: 执行结果(0:参数错误 1：执行成功 2：执行失败)
	 * 			]
	 * @throws Exception
	 */
	public Map<String, Object> insertField(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap)){//判断页面提交参数是否为空
			result.put("result", "0");
			return result;
		}
		strMap.put("TABLE", "TAB_4007");
		if(!MapUtil.compareKeyValue(strMap, "type", "2")){//向转换字段中添加字段
			strMap.put("TAB_046_007", "1");
			//判断是否存在拖动的字段
			String sql = "SELECT ID FROM TAB_4007 WHERE TAB_046_007='0' AND TAB_045_ID=param1 AND TAB_046_001=param2 AND TAB_046_002=param3 AND ID=param4"
				.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_ID")))
				.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_001")))
				.replaceAll("param3", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_002")))
				.replaceAll("param4", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
			List<List> existField = this.dbManager.executeQueryList(sql);
			String tab046Id = "";
			if(ListUtil.listIsNull(existField)){//如果不存在 先添加字段
				DataManageModel dataModel = new DataManageModel(this.dbManager);
				Map<String, String> insertResult = dataModel.saveSubmitInfo(strMap);//添加字段
				if(MapUtil.compareKeyValue(insertResult, "result", "1")){//判断是否执行失败
					result.put("result", "2");
					return result;
				}
				tab046Id = MapUtil.get(insertResult, "record")+"";
			}else{
				tab046Id = MapUtil.get(strMap, "TAB_046_ID")+"";
			}
			if(StringUtil.arrIsNULL(tab046Id)){
				result.put("result", "2");
				return result;
			}
			//向转换格式表中添加字段信息
			sql = "INSERT INTO TAB_4010(TAB_046_ID, TAB_046_ID_01) VALUES(param1, param2)"
				.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")))
				.replaceAll("param2", StringUtil.strAddQuote(tab046Id));
			String tab049Id = this.dbManager.executeInsertSQL(sql);
			if(StringUtil.arrIsNULL(tab049Id)){
				result.put("result", "2");
				return result;
			}
			result.put("result", "1");
		}else{//报表使用字段
			DataManageModel dataModel = new DataManageModel(this.dbManager);
			Map<String, String> insertResult = dataModel.saveSubmitInfo(strMap);
			if(MapUtil.compareKeyValue(insertResult, "result", "1")){
				result.put("result", "2");
				return result;
			}else{
				strMap.put("BEFORE", insertResult.get("record"));
				Map<String, Object> ordersResult = this.changeOrders(strMap);
				if(MapUtil.compareKeyValue(ordersResult, "result", "1")){
					result.put("result", "2");
					return result;
				}else{
					result.put("result", "1");
				}
			}
		}
		return result;
	}
	
	/**
	 * 字段转换格式设置
	 * @param strMap 页面提交参数
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 			]
	 * @throws Exception
	 */
	public Map<String, Object> fieldFormat(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_046_ID", "TAB_048_ID")){
			result.put("result", "0");
			return result;
		}
		String sql = "DELETE FROM TAB_4010 WHERE TAB_046_ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
		this.dbManager.executeUpdateSQL(sql);
		sql = "INSERT INTO TAB_4010(TAB_046_ID,TAB_046_ID_01, TAB_048_ID) VALUES(param1, param1, param2)"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_048_ID")));
		String tab049Id = this.dbManager.executeInsertSQL(sql);
		if(tab049Id.equals("-1")){
			result.put("result", "2");
			return result;
		}
		sql = "UPDATE TAB_4007 SET TAB_046_005='{formatparam2}' WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")))
			.replaceAll("param2", tab049Id);
		if(this.dbManager.executeUpdateSQL(sql)>0){
			result.put("result", "1");
		}else{
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 修改字段转换类型
	 * @param strMap 页面提交参数
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 			]
	 * @throws Exception
	 */
	private Map<String, Object> editFieldType(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_046_ID", "TAB_047_ID")){
			result.put("result", "0");
			return result;
		}
		//删除之前的字段转换格式
		String sql = "DELETE FROM TAB_4010 WHERE TAB_046_ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
		this.dbManager.executeUpdateSQL(sql);
		sql = "INSERT INTO TAB_4010(TAB_046_ID,TAB_046_ID_01, TAB_047_ID) VALUES(param1, param1, param2)"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_047_ID")));
		String tab049Id = this.dbManager.executeInsertSQL(sql);
		if(tab049Id.equals("-1")){
			result.put("result", "2");
			return result;
		}
		sql = "UPDATE TAB_4007 SET TAB_046_005=NULL WHERE ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
		if(this.dbManager.executeUpdateSQL(sql)>0){
			result.put("result", "1");
		}else{
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 保存字段转换信息
	 * @param strMap 页面提交信息
	 * @return
	 * 		Map[
	 * 			result	: 执行结果 
	 * 			(
	 * 				0	: 参数错误
	 * 				1	: 执行成功
	 * 				2	: 执行失败
	 * 			)
	 * 		]
	 * @throws Exception
	 */
	public Map<String, Object> saveFormat(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_046_ID")){//判断字段配置ID是否为空
			result.put("result", "0");
			return result;
		}
		String sql = "";
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_049_IDS")){//判断是否存在使用的转换字段 如果存在 删除未使用的字段
			sql = "DELETE FROM TAB_4007 WHERE ID IN (SELECT TAB_046_ID_01 FROM TAB_4010 WHERE TAB_046_ID=param1 AND TAB_049_001 NOT IN(param2)) AND ID<>param1"
				.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")))
				.replaceAll("param2", StringUtil.str2QuoteStr(MapUtil.get(strMap, "TAB_049_IDS")+""));
			this.dbManager.executeUpdateSQL(sql);
		}
		//更新字段转换内容
		sql = "UPDATE TAB_4007 SET TAB_046_005=param1 WHERE ID=param2"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_005")))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_046_ID")));
		if(this.dbManager.executeUpdateSQL(sql)>0){//更新成功
			result.put("result", "1");
		}else{//更新失败
			result.put("result", "2");
		}
		return result;
	}
	
	/**
	 * 功能操作
	 * @param strMap 页面提交参数
	 * 		type	: 功能类型
	 * @return
	 * 		Map[
	 * 				result	: 执行结果(0:参数错误	1:执行成功	2:执行失败)
	 * 			]
	 * @throws Exception
	 */
	public Map<String, Object> operate(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			result.put("result", "0");
			return result;
		}
		switch (ObjectUtil.obj2Int(MapUtil.get(strMap, "type"))) {
		case 1://修改字段转换类型
			result = this.editFieldType(strMap);
			break;
		case 2://将同一个报表的信息复制到另一个环节的该报表上
			result = this.copyTempletConfig(strMap);
			break;
		case 3://查询报表配置ID
			result = this.reportConfig(strMap);
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * 将同一个报表的信息复制到另一个环节的该报表上
	 * @param strMap 页面提交参数
	 * @return
	 * 		Map[
	 * 			result	: 执行结果
	 * 			(
	 * 				0	: 参数错误 
	 * 				1	: 执行成功 
	 * 				2	: 执行失败
	 * 			)
	 * 		]
	 * @throws Exception
	 */
	private Map<String, Object> copyTempletConfig(Map<String, String> strMap) throws Exception{
		Map<String, Object> result = new Hashtable<String, Object>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_045_ID", "TAB_045_001", "TAB_045_002")){//判断报表配置ID TAB_045_ID，业务ID TAB_045_001，环节ID TAB_045_002是否为空
			result.put("result", "0");//参数错误
			return result;
		}
		//查询模板在模板管理中的ID及模板名（模板文件名）
		String sql = "SELECT T2.ID,T2.TAB_010_001 FROM TAB_4006 T1 RIGHT JOIN TAB_4003 T2 ON T2.ID=T1.TAB_010_ID WHERE T1.ID=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_ID")));
		List<List> tab010List = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(tab010List)){//如果模板不存在 直接返回
			result.put("result", "0");
			return result;
		}
		//删除在模板详细配置中的信息
		sql = "DELETE FROM TAB_4006 WHERE TAB_010_ID=param1 AND TAB_045_001=param2 AND TAB_045_002=param3"
			.replaceAll("param1", StringUtil.strAddQuote(tab010List.get(0).get(0)))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_001")))
			.replaceAll("param3", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_002")));
		this.dbManager.executeUpdateSQL(sql);
		//重新向模板详细配置中插入模板配置信息
		sql = "INSERT INTO TAB_4006(TAB_010_ID,TAB_045_001,TAB_045_002,TAB_045_005) VALUES(param1, param2, param3, '1')"
			.replaceAll("param1", StringUtil.strAddQuote(tab010List.get(0).get(0)))
			.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_001")))
			.replaceAll("param3", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_002")));
		String tab045ID = this.dbManager.executeInsertSQL(sql);
		if(tab045ID.equals("-1")){//如果插入失败 返回
			result.put("result", "2");
			return result;
		}
		//查询复制的报表模板信息
		sql = "SELECT ID, tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007 FROM TAB_4007 WHERE tab_045_id=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_ID")));
		List<List> tab046List = this.dbManager.executeQueryList(sql);//如果被复制的报表模板为配置信息，则直接返回
		if(ListUtil.listIsNull(tab046List)){
			result.put("result", "0");
			return result;
		}
		Map<String, List> tab046Map = new Hashtable<String, List>();
		for(int i=0; i<tab046List.size(); i++){
			tab046Map.put(tab046List.get(i).get(0)+"", tab046List.get(i));
		}
		//查询报表字段是否存在转换信息
		sql = "SELECT T2.TAB_046_ID FROM TAB_4007 T1 RIGHT JOIN TAB_4010 T2 ON T2.TAB_046_ID = T1.ID WHERE T1.TAB_045_ID=param1 GROUP BY T2.TAB_046_ID"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_ID")));
		List<List> tab049FilterList = this.dbManager.executeQueryList(sql);
		//如果报表存在转换的字段
		for(int i=0; i<tab049FilterList.size(); i++){
			List singleInfo = tab046Map.get(tab049FilterList.get(i).get(0)+"");
			tab046Map.remove(tab049FilterList.get(i).get(0)+"");//移除字段Map中存在的该字段
			//向报表详细配置的字段表中插入字段信息
			sql = "INSERT INTO TAB_4007(tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007,tab_045_id) VALUES(param1,param2,param3, param4, param5,param6, param7,param8)"
				.replaceAll("param1", StringUtil.strAddQuote(singleInfo.get(1)))
				.replaceAll("param2", StringUtil.strAddQuote(singleInfo.get(2)))
				.replaceAll("param3", StringUtil.strAddQuote(singleInfo.get(3)))
				.replaceAll("param4", StringUtil.strAddQuote(singleInfo.get(4)))
				.replaceAll("param5", StringUtil.strAddQuote(singleInfo.get(5)))
				.replaceAll("param6", StringUtil.strAddQuote(singleInfo.get(6)))
				.replaceAll("param7", StringUtil.strAddQuote(singleInfo.get(7)))
				.replaceAll("param8", StringUtil.strAddQuote(tab045ID));
			String tab046ID = this.dbManager.executeInsertSQL(sql);
			if(tab046ID.equals("-1")){//插入是否成功 如果不成功 返回
				result.put("result", "2");
				return result;
			}
			//查询关联的转换字段
			sql = "SELECT tab_046_id_01, tab_047_id, tab_048_id, tab_049_001 FROM TAB_4010 WHERE tab_046_id=param1 AND tab_046_id_01<>param1"
				.replaceAll("param1", StringUtil.strAddQuote(tab049FilterList.get(i).get(0)));
			List<List> subnodeList = this.dbManager.executeQueryList(sql);
			//如果存在关联的转换字段
			for(int j=0; j<subnodeList.size(); j++){
				List subSingleInfo = tab046Map.get(subnodeList.get(j).get(0)+"");
				tab046Map.remove(subnodeList.get(j).get(0)+"");
				//插入字段内容
				sql = "INSERT INTO TAB_4007(tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007,tab_045_id) VALUES(param1,param2,param3, param4, param5,param6, param7,param8)"
					.replaceAll("param1", StringUtil.strAddQuote(subSingleInfo.get(1)))
					.replaceAll("param2", StringUtil.strAddQuote(subSingleInfo.get(2)))
					.replaceAll("param3", StringUtil.strAddQuote(subSingleInfo.get(3)))
					.replaceAll("param4", StringUtil.strAddQuote(subSingleInfo.get(4)))
					.replaceAll("param5", StringUtil.strAddQuote(subSingleInfo.get(5)))
					.replaceAll("param6", StringUtil.strAddQuote(subSingleInfo.get(6)))
					.replaceAll("param7", StringUtil.strAddQuote(subSingleInfo.get(7)))
					.replaceAll("param8", StringUtil.strAddQuote(tab045ID));
				String tab046ID_01 = this.dbManager.executeInsertSQL(sql);
				if(tab046ID_01.equals("-1")){
					result.put("result", "2");
					return result;
				}
				//插入配置信息
				sql = "INSERT INTO TAB_4010(TAB_046_ID,TAB_046_ID_01,TAB_047_ID,TAB_048_ID,tab_049_001) VALUES(param4, param5, param1, param2, param3)"
					.replaceAll("param1", StringUtil.strAddQuote(subnodeList.get(j).get(1)))
					.replaceAll("param2", StringUtil.strAddQuote(subnodeList.get(j).get(2)))
					.replaceAll("param3", StringUtil.strAddQuote(subnodeList.get(j).get(3)))
					.replaceAll("param4", StringUtil.strAddQuote(tab046ID))
					.replaceAll("param5", StringUtil.strAddQuote(tab046ID_01));
				if(this.dbManager.executeInsertSQL(sql).equals("-1")){
					result.put("result", "2");
					return result;
				}
			}
		}
		//对于没有配置转换信息的字段
		Set<String> keySet = tab046Map.keySet();
		for(String s: keySet){
			List singleInfo = tab046Map.get(s);
			sql = "INSERT INTO TAB_4007(tab_046_001, tab_046_002, tab_046_003, tab_046_004, tab_046_005, tab_046_006, tab_046_007,tab_045_id) VALUES(param1,param2,param3, param4, param5,param6, param7,param8)"
				.replaceAll("param1", StringUtil.strAddQuote(singleInfo.get(1)))
				.replaceAll("param2", StringUtil.strAddQuote(singleInfo.get(2)))
				.replaceAll("param3", StringUtil.strAddQuote(singleInfo.get(3)))
				.replaceAll("param4", StringUtil.strAddQuote(singleInfo.get(4)))
				.replaceAll("param5", StringUtil.strAddQuote(singleInfo.get(5)))
				.replaceAll("param6", StringUtil.strAddQuote(singleInfo.get(6)))
				.replaceAll("param7", StringUtil.strAddQuote(singleInfo.get(7)))
				.replaceAll("param8", StringUtil.strAddQuote(tab045ID));
			if(this.dbManager.executeInsertSQL(sql).equals("-1")){
				result.put("result", "2");
				return result;
			}
		}
		//查询被复制的报表是否存在排序信息
		sql = "SELECT tab_050_001, tab_050_002, tab_050_003, tab_050_004 FROM TAB_4011 WHERE tab_045_id=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_ID")));
		List<List> tab050List = this.dbManager.executeQueryList(sql);
		//存在排序信息
		for(int i=0;i<tab050List.size(); i++){
			//插入排序信息
			sql = "INSERT INTO TAB_4011(tab_050_001, tab_050_002, tab_050_003, tab_050_004, TAB_045_ID) VALUES(param1, param2, param3, param4, param5)"
				.replaceAll("param1", StringUtil.strAddQuote(tab050List.get(i).get(0)))
				.replaceAll("param2", StringUtil.strAddQuote(tab050List.get(i).get(1)))
				.replaceAll("param3", StringUtil.strAddQuote(tab050List.get(i).get(2)))
				.replaceAll("param4", StringUtil.strAddQuote(tab050List.get(i).get(3)))
				.replaceAll("param5", StringUtil.strAddQuote(tab045ID));
			if(this.dbManager.executeInsertSQL(sql).equals("-1")){//是否执行成功
				result.put("result", "2");
				return result;
			}
		}
		//查看是否存在过滤条件
		sql = "SELECT tab_051_001, tab_051_002 FROM TAB_4012 WHERE tab_045_id=param1"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_045_ID")));
		List<List> tab051List = this.dbManager.executeQueryList(sql);
		//存在过滤条件就循环
		for(int i=0; i<tab051List.size(); i++){
			//插入报表过滤条件
			sql = "INSERT INTO TAB_4012(tab_051_001, tab_051_002, TAB_045_ID) VALUES(param1, param2, param3)"
				.replaceAll("param1", StringUtil.strAddQuote(tab051List.get(i).get(0)))
				.replaceAll("param2", StringUtil.strAddQuote(tab051List.get(i).get(1)))
				.replaceAll("param3", StringUtil.strAddQuote(tab045ID));
			if(this.dbManager.executeInsertSQL(sql).equals("-1")){//执行是否成功
				result.put("result", "2");
				return result;
			}
		}
		result.put("result", "1");
		return result;
	}
}
