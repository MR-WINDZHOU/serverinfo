package com.shxy.www.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.shxy.www.conf.DBManager;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.StringUtil;

public class TempletManageModel {
	
	private DBManager dbManager = null;
	
	public TempletManageModel(DBManager dbManager) {
		super();
		this.dbManager = dbManager;
	}

	/**
	 * 添加模板文件
	 * @param strMap	: 页面提交的字符串类型参数
	 * @param fileMap	: 页面提交的文件类型的参数
	 * @return
	 * 		Map[
	 * 			result	: 执行结果(1=成功 0=参数错误)
	 * 			record	: 如果是插入操作 返回插入的主键ID
	 *		]
	 * @throws Exception
	 */
	public synchronized Map<String, String> addTemplet(Map<String, String> strMap, Map<String, File> fileMap) throws Exception{
		Map<String, String> result = new Hashtable<String, String>();
		if(!MapUtil.checkMapParamsNotNULL(strMap, "TAB_010_002")){//判断模板显示名是否为空
			result.put("result", "0");
			return result;
		}
		//如果模板已存在
		if(MapUtil.checkMapParamsNotNULL(strMap, "TAB_010_001_NAME")){
			if(MapUtil.checkMapParamsNotNULL(strMap, "TAB_010_001")){//存在上传的模板文件 则更新模板文件对象
				File templetFile = fileMap.get("TAB_010_001");
				String TAB_010_001_NAME = strMap.get("TAB_010_001_NAME");
				String path = strMap.get("PATH");
				//输出birt文件到服务器中
				OutputStream out = new FileOutputStream(path+"reports"+File.separatorChar+TAB_010_001_NAME);
				InputStream in = new FileInputStream(templetFile);
				byte[] blobbytes = new byte[1024];
				int readLength = 0;
				while ((readLength = in.read(blobbytes)) != -1) {
				    out.write(blobbytes, 0, readLength);
				}
				out.flush();
				out.close();
				in.close();
			}
			//更新模板显示名称
			String sql = "UPDATE TAB_4003 SET TAB_010_002=param1,IS_EXCEL=param3,ISSYNCHRONOUS=param4 WHERE ID=param2"
				.replaceAll("param1", StringUtil.strAddQuote(MapUtil.get(strMap, "TAB_010_002")))
				.replaceAll("param2", StringUtil.strAddQuote(MapUtil.get(strMap, "ID")))
				.replaceAll("param3", StringUtil.strAddQuote(MapUtil.get(strMap, "IS_EXCEL")))
				.replaceAll("param4", StringUtil.strAddQuote(MapUtil.get(strMap, "ISSYNCHRONOUS")))
				;
			this.dbManager.executeUpdateSQL(sql);
		}else{
			File templetFile = fileMap.get("TAB_010_001");//获得上传的模板文件
			String path = strMap.get("PATH");//获得项目路径
			String filePath = strMap.get("TAB_010_001");//获得上传的文件路径
			String fileName = filePath.substring(filePath.lastIndexOf(File.separatorChar)+1);//获得上传的文件名
			//输出birt文件到服务器中
			OutputStream out = new FileOutputStream(path+"reports"+File.separatorChar+fileName);
			InputStream in = new FileInputStream(templetFile);
			byte[] blobbytes = new byte[1024];
			int readLength = 0;
			while ((readLength = in.read(blobbytes)) != -1) {
			    out.write(blobbytes, 0, readLength);
			}
			out.flush();
			out.close();
			in.close();
			//将模板添加到模板库中(TAB_010)
			String sql = "INSERT INTO TAB_4003(TAB_010_001, TAB_010_002,ISSYNCHRONOUS,IS_EXCEL) VALUES(param1, param2,param3,param4)"
				.replaceAll("param1", StringUtil.strAddQuote(fileName))
				.replaceAll("param2", StringUtil.strAddQuote(strMap.get("TAB_010_002")))
				.replaceAll("param3", StringUtil.strAddQuote(strMap.get("ISSYNCHRONOUS")))
				.replaceAll("param4", StringUtil.strAddQuote(strMap.get("IS_EXCEL")))
				;
			String record = this.dbManager.executeInsertSQL(sql);
			sql = "select ID from TAB_4005 where tab_012_001 like '%param1'"
				.replaceAll("param1", strMap.get("TAB_011_001"));
			List<List> tab012Id = this.dbManager.executeQueryList(sql);
			if(ListUtil.listIsNull(tab012Id)&&!record.equals("-1")){
				result.put("result", "2");
				return result;
			}
			sql = "INSERT INTO TAB_4004(TAB_011_001,TAB_011_002) VALUES(param1,param2)"
				.replaceAll("param1", StringUtil.strAddQuote(tab012Id.get(0).get(0).toString()))
				.replaceAll("param2", StringUtil.strAddQuote(record));
			this.dbManager.executeInsertSQL(sql);
			result.put("record", record);
		}
		result.put("result", "1");
		return result;
	} 
}
