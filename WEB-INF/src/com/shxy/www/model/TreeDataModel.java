package com.shxy.www.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.StringUtil;

public class TreeDataModel {

	private DBManager dbManager = null;

	public TreeDataModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}
	
	/**
	 * 获得Oracle数据库中的tree信息
	 * @param strMap 页面提交信息
	 * 		type	: tree类型
	 * @return
	 * 		tree信息
	 * @throws Exception
	 */
	public List<Map<String, Object>> tree(Map<String, String> strMap) throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		strMap.put("type", "1");
		if(!MapUtil.checkMapParamsNotNULL(strMap, "type")){
			return result;
		}
		
		switch (ObjectUtil.obj2Int(MapUtil.get(strMap, "type"))) {
		case 1://业务环节树（不包含模板节点）
			result = this.businessNodeTree(strMap);
			break;
		case 2://业务树
			result = this.businessTree(strMap);
			break;
		case 3://模板树
			result = this.templetsTree(strMap);
			break;
		case 4://获得业务节点树信息
			result = this.businessNodesTree(strMap);
			break;
		}
		return result;
	}
	
	/**
	 * 获得本地数据库文件树信息
	 * @param strMap 页面提交信息
	 * 		type	: 树类型
	 * @return
	 * 		tree信息
	 */
	public List<Map<String, Object>> sqliteTree(Map<String, String> strMap){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		switch (ObjectUtil.obj2Int(MapUtil.get(strMap, "type"))) {
		case 1:
			break;
		}
		return result;
	}
	
	/**
	 * 业务环节树（不包含模板节点）
	 * @param strMap 页面提交信息
	 * 		node	: 节点信息
	 * @return
	 * 		Map[
	 * 			id		: 节点ID
	 * 			text	: 节点名称
	 * 			business: 业务ID
	 * 			leaf	: 是否叶子节点(true: 叶子, false: 非叶子)
	 * 		]
	 * @throws Exception
	 */
	private List<Map<String, Object>> businessNodeTree(Map<String, String> strMap) throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		/*if(!MapUtil.checkMapParamsNotNULL(strMap, "node")){
			return result;
		}*/
		String node = MapUtil.get(strMap, "node").toString();
		String[] nodes = node.split("@");
		String sql = "";
		Map<String, Object> nodeInfo = new HashMap<String, Object>();
		nodeInfo.put("id", "111");//添加"@0"，是为了防止tree的ID重复
		nodeInfo.put("text", "1111");
		result.add(nodeInfo);
		nodeInfo = new HashMap<String, Object>();
		nodeInfo.put("id", "222");//添加"@0"，是为了防止tree的ID重复
		nodeInfo.put("text", "3333");
		result.add(nodeInfo);
		nodeInfo = new HashMap<String, Object>();
		nodeInfo.put("id", "4444");//添加"@0"，是为了防止tree的ID重复
		nodeInfo.put("text", "3333");
		nodeInfo.put("leaf", true);
		result.add(nodeInfo);
		if(node.equalsIgnoreCase("business")){//如果上级节点是根节点 那么加载一级节点（业务类型）
			sql = "select ZGA000001,ZGA000002 from ZGA000 where ZGA000003='1' order by ZGA000004";
			List<List> nodeList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<nodeList.size(); i++){
				if(ObjectUtil.arrIsNULL(nodeList.get(i).get(0), nodeList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				nodeInfo = new HashMap<String, Object>();
				nodeInfo.put("id", nodeList.get(i).get(0)+"@0");//添加"@0"，是为了防止tree的ID重复
				nodeInfo.put("text", nodeList.get(i).get(1));
				result.add(nodeInfo);
			}
		}else if(node.endsWith("@0")){//加载二级节点（业务节点）
			sql = "SELECT T3.WF05001,T3.WF05002 FROM ZGA001 T1 RIGHT JOIN ZGA002 T2 ON T1.ZGA001001=T2.ZGA001001 RIGHT JOIN WF05 T3 ON T3.WF05001=T2.ZGA002002 WHERE T1.ZGA001002 LIKE '%,param1' ORDER BY T3.WF05007"
				.replaceAll("param1", nodes[0]);
			List<List> leftList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<leftList.size(); i++){
				if(ObjectUtil.arrIsNULL(leftList.get(i).get(0), leftList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> LeftInfo = new HashMap<String, Object>();
				LeftInfo.put("id", leftList.get(i).get(0)+"@1");//添加"@1"，是为了防止tree的ID重复
				LeftInfo.put("text", leftList.get(i).get(1));
				result.add(LeftInfo);
			}
		}else if(node.endsWith("@1")){//加载三级节点（环节节点）
			sql = "SELECT T2.WF07001,T2.WF07002 FROM WF06 T1 RIGHT JOIN WF07 T2 ON T1.WF06001=T2.WF07005 WHERE T1.WF06005=param1 ORDER BY T2.WF07001"
				.replaceAll("param1", StringUtil.strAddQuote(nodes[0]));
			List<List> leftList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<leftList.size(); i++){
				if(ObjectUtil.arrIsNULL(leftList.get(i).get(0), leftList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> leftInfo = new HashMap<String, Object>();
				leftInfo.put("id", leftList.get(i).get(0));
				leftInfo.put("text", leftList.get(i).get(1));
				leftInfo.put("business", nodes[0]);
				leftInfo.put("leaf", true);
				result.add(leftInfo);
			}
		}
		return result;
	}
	
	/**
	 * 业务树
	 * @param strMap 页面提交信息
	 * 		node	: 节点信息
	 * @return
	 * 		Map[
	 * 			id		: 节点ID
	 * 			text	: 节点名称
	 * 			business: 业务ID
	 * 			leaf	: 是否叶子节点(true: 叶子, false: 非叶子)
	 * 		]
	 * @throws Exception
	 */
	private List<Map<String, Object>> businessTree(Map<String, String> strMap) throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "node")){
			return result;
		}
		String node = MapUtil.get(strMap, "node").toString();
		String sql = "";
		if(node.equalsIgnoreCase("business")){//记载一级节点（业务类型）
			sql = "select ZGA000001,ZGA000002 from ZGA000 where ZGA000003='1' order by ZGA000004";
			List<List> nodeList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<nodeList.size(); i++){
				if(ObjectUtil.arrIsNULL(nodeList.get(i).get(0), nodeList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> nodeInfo = new HashMap<String, Object>();
				nodeInfo.put("id", nodeList.get(i).get(0));
				nodeInfo.put("text", nodeList.get(i).get(1));
				result.add(nodeInfo);
			}
		}else{//加载叶子节点（业务节点--叶子节点）
			sql = "SELECT T3.WF05001,T3.WF05002 FROM ZGA001 T1 RIGHT JOIN ZGA002 T2 ON T1.ZGA001001=T2.ZGA001001 RIGHT JOIN WF05 T3 ON T3.WF05001=T2.ZGA002002 WHERE T1.ZGA001002 LIKE '%,param1' ORDER BY T3.WF05007"
				.replaceAll("param1", node);
			List<List> leftList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<leftList.size(); i++){
				if(ObjectUtil.arrIsNULL(leftList.get(i).get(0), leftList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> leftInfo = new HashMap<String, Object>();
				leftInfo.put("id", leftList.get(i).get(0)+"@business");
				leftInfo.put("text", leftList.get(i).get(1));
				leftInfo.put("business", leftList.get(i).get(0));
				leftInfo.put("leaf", true);
				result.add(leftInfo);
			}
		}
		return result;
	}
	
	/**
	 * 模板树
	 * @param strMap 页面提交信息
	 * 		node	: 节点信息
	 * @return
	 * 		Map[
	 * 			id		: 节点ID
	 * 			text	: 节点名称
	 * 			birt	: TAB_045表ID
	 * 			leaf	: 是否叶子节点(true: 叶子, false: 非叶子)
	 * 		]
	 * @throws Exception
	 */
	private List<Map<String, Object>> templetsTree(Map<String, String> strMap) throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "node")){
			return result;
		}
		String node = strMap.get("NODE");
		if(node.equals("templet")){//如果是根节点 则node为1（它的上级节点ID为1）
			node="1";
		}
		//判断是否存在非叶子节点（模板管理）
		String sql = "SELECT ID,TAB_008_001 FROM TAB_4001 WHERE TAB_008_002=param1"
			.replaceAll("param1", StringUtil.strAddQuote(node));
		List<List> nodeList = this.dbManager.executeQueryList(sql);
		for(int i=0; i<nodeList.size(); i++){
			if(ObjectUtil.arrIsNULL(nodeList.get(i).get(0), nodeList.get(i).get(1))){//如果某个参数为空 ，返回
				continue;
			}
			Map<String, Object> nodeInfo = new HashMap<String, Object>();
			nodeInfo.put("id", nodeList.get(i).get(0));
			nodeInfo.put("text", nodeList.get(i).get(1));
			result.add(nodeInfo);
		}
		//判断是否存在模板节点（叶子节点）
		sql = "SELECT T3.ID,T3.TAB_010_001,T3.TAB_010_002 FROM TAB_4005 T1 LEFT JOIN TAB_4004 T2 ON T2.TAB_011_001=T1.ID LEFT JOIN TAB_4003 T3 ON T3.ID=T2.TAB_011_002 WHERE T1.TAB_012_001 LIKE '%,param1'"
			.replaceAll("param1", node);
		List<List> leftList = this.dbManager.executeQueryList(sql);
		for(int i=0; i<leftList.size(); i++){
			if(ObjectUtil.arrIsNULL(leftList.get(i).get(0), leftList.get(i).get(1), leftList.get(i).get(2))){//如果某个参数为空 ，返回
				continue;
			}
			Map<String, Object> LeftInfo = new HashMap<String, Object>();
			LeftInfo.put("id", leftList.get(i).get(0));
			LeftInfo.put("birt", leftList.get(i).get(1));
			LeftInfo.put("text", leftList.get(i).get(2));
			LeftInfo.put("leaf", true);
			result.add(LeftInfo);
		}
		return result;
	}
	
	/**
	 * 获得业务节点树信息
	 * @param strMap 页面提交信息
	 * 		node	: 节点信息
	 * @return
	 * 		Map[
	 * 			id		: 节点ID
	 * 			text	: 节点名称
	 * 			drop	: 是否可拖动到此节点(true: 可以)
	 * 			type	: 节点类型
	 * 			leaf	: 是否叶子节点(true: 叶子, false: 非叶子)
	 * 			start	: 是否启用
	 * 			business: 业务ID
	 * 			node	: 环节ID
	 * 			config	: TAB_045表ID
	 * 		]
	 * @throws Exception
	 */
	private List<Map<String, Object>> businessNodesTree(Map<String, String> strMap) throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "node")){
			return result;
		}
		long sysDate = new Date().getTime();
		String node = strMap.get("NODE");
		String[] nodes = node.split("@");
		String sql = "";
		if(node.equalsIgnoreCase("business")){//加载一级节点（业务类型节点）
			sql = "select ZGA000001,ZGA000002 from ZGA000 where ZGA000003='1' order by ZGA000004";
			List<List> nodeList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<nodeList.size(); i++){
				if(ObjectUtil.arrIsNULL(nodeList.get(i).get(0), nodeList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> nodeInfo = new HashMap<String, Object>();
				nodeInfo.put("id", nodeList.get(i).get(0)+"@"+sysDate+i+"@0");
				nodeInfo.put("text", nodeList.get(i).get(1));
				nodeInfo.put("type", "6");
				result.add(nodeInfo);
			}
		}else if(node.endsWith("@0")){//加载二级节点（业务节点）
			sql = "SELECT T3.WF05001,T3.WF05002 FROM ZGA001 T1 RIGHT JOIN ZGA002 T2 ON T1.ZGA001001=T2.ZGA001001 RIGHT JOIN WF05 T3 ON T3.WF05001=T2.ZGA002002 WHERE T1.ZGA001002 LIKE '%,param1'"
				.replaceAll("param1", nodes[0]);
			List<List> leftList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<leftList.size(); i++){
				if(ObjectUtil.arrIsNULL(leftList.get(i).get(0), leftList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> LeftInfo = new HashMap<String, Object>();
				LeftInfo.put("id", leftList.get(i).get(0)+"@"+sysDate+i+"@1");
				LeftInfo.put("text", leftList.get(i).get(1));
				LeftInfo.put("type", "5");
				result.add(LeftInfo);
			}
		}else if(node.endsWith("@1")){//加载三级节点（环节节点）
			sql = "SELECT T2.WF07001,T2.WF07002 FROM WF06 T1 RIGHT JOIN WF07 T2 ON T1.WF06001=T2.WF07005 WHERE T1.WF06005=param1 ORDER BY T2.WF07001"
				.replaceAll("param1", StringUtil.strAddQuote(nodes[0]));
			List<List> leftList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<leftList.size(); i++){
				if(ObjectUtil.arrIsNULL(leftList.get(i).get(0), leftList.get(i).get(1))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> LeftInfo = new HashMap<String, Object>();
				LeftInfo.put("id", leftList.get(i).get(0)+"@"+nodes[0]+"@"+sysDate+i+"@2");
				LeftInfo.put("text", leftList.get(i).get(1));
				LeftInfo.put("type", "4");
				LeftInfo.put("drop", true);
				result.add(LeftInfo);
			}
		}else if(node.endsWith("@2")){//加载四级节点（模板节点--叶子节点）
			sql = " SELECT T2.ID,T2.TAB_010_001,T2.TAB_010_002,NVL(T3.TAB_045_005, 0),NVL(T3.ID, 0) FROM TAB_4002 T1 RIGHT JOIN TAB_4003 T2 ON T1.TAB_009_001 = T2.ID LEFT JOIN TAB_4006 T3 ON T2.ID=T3.TAB_010_ID AND T3.TAB_045_001=param2 AND T3.TAB_045_002=param1 WHERE T1.TAB_009_002 =param1 ORDER BY T2.ID"
				.replaceAll("param1", StringUtil.strAddQuote(nodes[0]))
				.replaceAll("param2", StringUtil.strAddQuote(nodes[1]));
			List<List> leftList = this.dbManager.executeQueryList(sql);
			for(int i=0; i<leftList.size(); i++){
				if(ObjectUtil.arrIsNULL(leftList.get(i).get(0), leftList.get(i).get(1), leftList.get(i).get(2))){//如果某个参数为空 ，返回
					continue;
				}
				Map<String, Object> LeftInfo = new HashMap<String, Object>();
				LeftInfo.put("id", leftList.get(i).get(0)+"@"+sysDate+i+"@3");
				LeftInfo.put("birt", leftList.get(i).get(1));
				LeftInfo.put("node", nodes[0]);
				LeftInfo.put("business", nodes[1]);
				LeftInfo.put("text", leftList.get(i).get(2));
				LeftInfo.put("start", leftList.get(i).get(3));
				LeftInfo.put("config", leftList.get(i).get(4));
				LeftInfo.put("leaf", true);
				result.add(LeftInfo);
			}
		}
		return result;
	}
}
