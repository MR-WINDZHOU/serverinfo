package com.shxy.www.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ResultSetUtil {
	
	/**
	 *  获得ResultSet中的数据
	 * @param rs
	 * @return
	 * 		List<List>
	 */
	public List<List> getTableSQLList(ResultSet rs) {
		List<List> tableInfo = new Vector<List>();
		try {
			ResultSetMetaData rsm = rs.getMetaData();
			int rowNum = rsm.getColumnCount();//获得每行数据个数
			while (rs.next()) {//循环行数据
				List<Object> list = new Vector<Object>();
				for (int i = 1; i <= rowNum; i++) {//循环列数据
					Object obj = ObjectUtil.obj2Str(rs.getObject(i));
					list.add(obj);
				}
				tableInfo.add(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableInfo;
	}
	
	
	/**
	 * 迭代ResultSet集合中每条记录,然后返回该所有记录的Map集合
	 * @param rs ResultSet集合
	 * @return List 所有记录的Map<String,Object>集合
	 */
	public static List<Map<String, Object>> readResultSetMap(ResultSet rs){
		List<Map<String, Object>> list = new Vector<Map<String, Object>>();
		try {
			ResultSetMetaData rsm = rs.getMetaData();
			while (rs.next()) {
				Hashtable<String, Object> ht = new Hashtable<String, Object>();
				for (int i = 1; i <= rsm.getColumnCount(); i++) {
					ht.put(rsm.getColumnName(i).toUpperCase(), ObjectUtil.obj2Str(rs.getObject(i)));
				}
				list.add(ht);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
