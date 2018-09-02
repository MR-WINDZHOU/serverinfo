package com.shxy.www.model;

import java.util.Hashtable;
import java.util.Map;

import org.springframework.web.util.HtmlUtils;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.StringUtil;

/**
 *  birt调试记录日志类
 * @author admin
 *
 */
public class BirtLoggingModel {
	
	private DBManager dbManager = null;

	public BirtLoggingModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}
	
	public Map<String, String> logging(String author, String type, String content){
		return this.logging(author, type, content, "-1");
	}	
	
	/**
	 * 添加日志信息
	 * @param author 添加人
	 * @param type 日志类型
	 * @param content 日志内容
	 * @param tab045id 报表配置主键ID
	 * @return
	 * 		Map[
	 *		 			result	: 执行结果(0:参数错误 1：执行成功 2：执行失败)	
	 *				 ]
	 */
	public Map<String, String> logging(String author, String type, String content, String tab045id){
		Map<String, String> result = new Hashtable<String, String>();
		if(StringUtil.arrIsNULL(author, type, content)){
			result.put("result", "0");
			return result;
		}
		try {
			this.dbManager.beginTransaction();
			String sql = "INSERT INTO SYSTAB208(S001, S002, S003, S004) VALUES(param1, param2, param3, param4)"
				.replaceAll("param1", StringUtil.strAddQuotes(tab045id))
				.replaceAll("param2", StringUtil.strAddQuotes(author))
				.replaceAll("param3", StringUtil.strAddQuotes(type))
				.replaceAll("param4", StringUtil.strAddQuotes(HtmlUtils.htmlUnescape(content)));
			if(this.dbManager.executeInsertSQL(sql).equals("-1")){//插入失败
				result.put("result", "2");
				this.dbManager.rollback();
			}else{//插入成功
				result.put("result", "1");
				this.dbManager.commitTransaction();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("result", "2");
			this.dbManager.rollback();
		}
		return result;
	}
	
	/**
	 * 清空登录用户的日志信息
	 * @param author 要删除日志信息的登录用户
	 * @return
	 * 		Map[
	 *		 			result	: 执行结果(0:参数错误 1：执行成功)	
	 *				 ]
	 * @throws Exception
	 */
	public Map<String, String> clearLogging(String author) throws Exception{
		Map<String, String> result = new Hashtable<String, String>();
		if(StringUtil.arrIsNULL(author)){//如果author为空，返回
			result.put("result", "0");
			return result;
		}
		//删除对应登录用户的日志信息
		String sql = "DELETE FROM SYSTAB208 WHERE S002=param1"
			.replaceAll("param1", StringUtil.strAddQuotes(author));
		this.dbManager.executeUpdateSQL(sql);
		result.put("result", "1");
		return result;
	}
}
