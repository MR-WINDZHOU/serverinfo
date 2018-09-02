package com.shxy.www.conf;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.shxy.www.util.ListUtil;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.StringUtil;


public class OracleDataOperate extends AbstractDBOptionDAO{

	public OracleDataOperate(DBManager dbManager) {
		// TODO Auto-generated constructor stub
		super(dbManager);
	}
	
	@Override
	public Map<String, List> fieldsInTable(Map<String, String> strMap) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List> fieldsInfo = new Hashtable<String, List>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TABLE")){//判断参数中是否存在table，并且不能为空
			return fieldsInfo;
		}
		//查询table中的字段信息
		String sql = "SELECT UPPER(COLUMN_NAME) FROM USER_TAB_COLUMNS WHERE UPPER(TABLE_NAME)=UPPER(param1)"
			.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "table")));
		List<List> fieldsList = this.dbManager.executeQueryList(sql);
		if(ListUtil.listIsNull(fieldsList)){//如果不存在字段，直接返回
			return fieldsInfo;
		}
		//将字段信息保存到Map中，方便以后使用
		for(int i=0; i<fieldsList.size(); i++){
			fieldsInfo.put(ObjectUtil.obj2Str(fieldsList.get(i).get(0)), fieldsList.get(i));
		}
		return fieldsInfo;
	}
}