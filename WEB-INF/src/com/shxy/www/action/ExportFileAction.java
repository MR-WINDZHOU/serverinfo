package com.shxy.www.action;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.shxy.www.conf.DBManager;
import com.shxy.www.conf.OracleOfDBManager;
import com.shxy.www.model.ExportFileModel;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;

public class ExportFileAction extends ActionSupport {

	private static final long serialVersionUID = -5895143502298071570L;
	private DBManager dbManager = null;
	private ExportFileModel exportModel = null;
	private JSONObject jsonObject = new JSONObject();
	
	/**
	 * 导出文档
	 *
	 */
	public void export(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		try {
			String fileName = new SimpleDateFormat("yyyyMMddhhmmssS").format(new Date())+".sql";//默认导出的文件名
			
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			String IP = (String) session.getAttribute("IP");//获得用户连接的数据库IP
			String type = (String) session.getAttribute("type");//获得用户连接的数据库类型（0=基层，1=机关）
			String showName = (String) session.getAttribute("showName");//获得登录用户的显示名
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.exportModel = new ExportFileModel(this.dbManager);
				
				strMap.put("IP", IP);
				strMap.put("DBTYPE", type);
				strMap.put("SHOWNAME", showName);
				Map<String, Object> fileContent = this.exportModel.export(strMap);
				if(MapUtil.checkMapParamsNotNULL(fileContent, "title")){//如果返回的结果集中存在文件标题
					fileName = fileContent.get("title").toString();//获得文件标题
					//解决中文文件名乱码问题
					if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0){
						fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");//firefox浏览器
					}else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0){
						fileName = URLEncoder.encode(fileName, "UTF-8");//IE浏览器
					}
				}
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.setContentType("application/x-download;");
				
				out = response.getWriter();
				
				if(fileContent.containsKey("content")){//如果存在文件内容
					out.print(fileContent.get("content"));//输入文件内容
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getMessage());
		} finally {
			this.dbManager.close();//关闭conntion
			if(out!=null){//刷新并关闭文件输出流
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 本地数据库信息的导出到文件
	 *
	 */
	public void sqliteExport(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		try {
			
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			String author = (String)session.getAttribute("ID");//获得用户登录的ID
			strMap.put("AUTHOR", author);
			
			this.dbManager = new DBManager();
			this.exportModel = new ExportFileModel(this.dbManager);
			
			String fileName = new SimpleDateFormat("yyyyMMddhhmmssS").format(new Date());//默认导出文件名
			Map<String, Object> fileContent = this.exportModel.sqliteExport(strMap);
			if(MapUtil.checkMapParamsNotNULL(fileContent, "title")){//如果存在文件标题
				fileName = fileContent.get("title").toString();//获得文件标题名
				//解决中文文件名乱码问题
				if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0){
					fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");//firefox浏览器
				}else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0){
					fileName = URLEncoder.encode(fileName, "UTF-8");//IE浏览器
				}
			}
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName+".txt");
			response.setContentType("application/x-download;");
			out = response.getWriter();
			if(fileContent.containsKey("content")){
				out.print(fileContent.get("content").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getMessage());
		} finally {
			this.dbManager.close();//关闭conntion
			if(out!=null){
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 导出模板信息到文件
	 *
	 */
	public void exportTemplet(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		try {
			String fileName = new SimpleDateFormat("yyyyMMddhhmmssS").format(new Date());//默认文件名
			out = response.getWriter();
			
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			String author = (String)session.getAttribute("ID");//获得用户登录ID
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			Map<String, Object> fileContent = new Hashtable<String, Object>();
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.exportModel = new ExportFileModel(this.dbManager);
				
				fileContent = this.exportModel.exportTemplet(strMap);
				if(MapUtil.checkMapParamsNotNULL(fileContent, "title")){
					fileName = fileContent.get("title").toString();
					String showName = (String) session.getAttribute("showName");//获得登录用户的显示名
					fileName += showName+"-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					//解决中文文件名乱码问题
					if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0){
						fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");//firefox浏览器
					}else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0){
						fileName = URLEncoder.encode(fileName, "UTF-8");//IE浏览器
					}
				}
			}
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName+".sql");
			response.setContentType("application/x-download;");
			if(fileContent.containsKey("content")){
				out.print(fileContent.get("content"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getMessage());
		} finally {
			this.dbManager.close();
			if(out!=null){
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 删除报表模板
	 * 		视情况导出SQL到脚本文件中
	 *
	 */
	public void exportDelete(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		try {
			
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			if(!MapUtil.compareKeyValue(strMap, "export", "1")){//是否生成删除脚本文件 1=生成 0=不生成
				String fileName = new SimpleDateFormat("yyyyMMddhhmmssS").format(new Date());//获得默认文件名
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName+".sql");
				response.setContentType("application/x-download;");
			}
			out = response.getWriter();
			String IP = (String) session.getAttribute("IP");//获得用户连接数据库的IP
			String type = (String) session.getAttribute("type");//获得用户连接数据库的类型
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.exportModel = new ExportFileModel(this.dbManager);
				
				this.dbManager.beginTransaction();
				Map<String, Object> fileContent = this.exportModel.exportDelete(strMap);
				if(!MapUtil.compareKeyValue(strMap, "export", "1")&&fileContent.containsKey("content")){//如果要生成导出脚本文件并且存在导出内容
					out.print(fileContent.get("content"));//输出脚本到response流中
				}else{
					this.jsonObject = JSONObject.fromObject(fileContent);
					out.print(this.jsonObject.toString());
				}
				if(MapUtil.compareKeyValue(fileContent, "result", "1")){
					this.dbManager.rollback();
				}else{
					this.dbManager.commitTransaction();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getMessage());
		} finally {
			this.dbManager.close();
			if(out!=null){
				out.flush();
				out.close();
			}
		}
	}
}
