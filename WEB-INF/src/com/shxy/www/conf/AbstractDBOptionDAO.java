package com.shxy.www.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 藕旺江
 * @version V1.1
 * 修改时间 2010-10-01
 */
public class AbstractDBOptionDAO{
	
	protected DBManager dbManager = null;
	
	public AbstractDBOptionDAO(DBManager dbManager){
		this.dbManager = dbManager;
	}
	
	/**
	 * 读取选定表的字段信息
	 * @param strMap 执行时需要的参数Map
	 * 		必须参数 [[table, 表名]|[dbName, 数据库名]|...]
	 * @return
	 * 		字段信息集合[字段名, [[字段大小, 值]|...]|...]
	 * @throws Exception 
	 */
	public Map<String, List> fieldsInTable(Map<String, String> strMap) throws Exception{
		Map<String, List> fieldsInfo = new HashMap<String, List>();
		return fieldsInfo;
	}
	
	/**
	 * 获得链接对应的控制对象
	 * @param dbManager
	 * @return
	 * 		1:oracle数据操作对象
	 */
	public static AbstractDBOptionDAO getDBControl(DBManager dbManager){
		int type = DataOperateFactory.getDBType(dbManager);
		AbstractDBOptionDAO dbOption = new AbstractDBOptionDAO(dbManager); 
		switch(type){
		case 1:
			dbOption = new OracleDataOperate(dbManager);
			break;
		case 2:
			break;
		case 3:
			break;
		}
		return dbOption;
	}
}