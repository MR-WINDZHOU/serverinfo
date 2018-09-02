package com.shxy.www.action;

import java.io.File;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.springframework.web.util.HtmlUtils;

import com.opensymphony.xwork2.ActionSupport;
import com.shxy.www.conf.DBManager;
import com.shxy.www.conf.OracleOfDBManager;
import com.shxy.www.model.DataManageModel;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;

/**
 * 数据管理类
 * @author 藕旺江
 *
 */
public class DataManageAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private DBManager dbManager = null;
	private JSONObject jsonObject = new JSONObject();
	private DataManageModel dataModel = null;
	private String url = "";
	
	/**
	 * 保存form表单中的内容
	 * @return
	 */
	public String saveSubmitInfo(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		try {
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			String author = (String)session.getAttribute("ID");//获得用户登录的ID
			strMap.remove("AUTHOR");//删除重复提交的用户ID
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");//获得用户连接的数据库IP
			String type = (String) session.getAttribute("type");//获得用户连接的数据库类型
			if(StringUtil.arrIsNULL(IP, type)){
				return INPUT;
			}
			this.dbManager = new OracleOfDBManager(IP, type);
			this.dataModel = new DataManageModel(this.dbManager);
			this.dbManager.beginTransaction();
			Map<String, String> result = this.dataModel.saveSubmitInfo(strMap);//保存提交的信息
			request.setAttribute("result", result.get("result"));//将保存的信息放入request中
			if(!MapUtil.compareKeyValue(result, "result", "1")){//如果保存信息成功（result=1）
				this.dbManager.commitTransaction();
				request.setAttribute("record", result.get("record"));//如果是插入信息，将插入的记录主键ID放入request中
			}else{
				this.dbManager.rollback();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){//如果conntion不为空，关闭
				this.dbManager.close();
			}
		}
		return "editalert";
	}
	
	/**
	 * 以ajax的形式保存提交的内容到Oracle数据库中
	 * 
	 */
	public void saveSubmitInfoAjax(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		PrintWriter out = null;
		Map<String, String> resultMap = new Hashtable<String, String>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);//获得request中的所有参数信息
			String author = (String)session.getAttribute("ID");
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.dataModel = new DataManageModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.dataModel.saveSubmitInfo(strMap);
				if(!MapUtil.compareKeyValue(resultMap, "result", "1")){//如果提交的信息保存成功
					this.dbManager.commitTransaction();
				}else{
					this.dbManager.rollback();
				}
			}else{
				resultMap.put("result", "0");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			resultMap.put("result", "2");
			resultMap.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			//将参数信息以JSON的形式输出到request中
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 以ajax的形式保存提交的内容到Sqlite数据库中
	 * 
	 */
	public void saveSqliteAjax(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		PrintWriter out = null;
		Map<String, String> resultMap = new Hashtable<String, String>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);//获得用户提交的参数信息
			this.dbManager = new DBManager();
			this.dataModel = new DataManageModel(this.dbManager);
			this.dbManager.beginTransaction();
			resultMap = this.dataModel.saveSqliteInfo(strMap);//保存信息到Sqlite数据库中
			if(!MapUtil.compareKeyValue(resultMap, "result", "1")){//如果保存成功
				this.dbManager.commitTransaction();
			}else{//保存失败
				this.dbManager.rollback();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			resultMap.put("result", "2");
			resultMap.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			//将保存的信息以JSON形式返回的页面
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 删除对应表的数据
	 * @return
	 */
	public String delete(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String author = (String) session.getAttribute("ID");
		Map<String, Object> result = new Hashtable<String, Object>();
		try {
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.dataModel = new DataManageModel(this.dbManager);
				this.dbManager.beginTransaction();
				result = this.dataModel.delete(strMap);
				if(!MapUtil.compareKeyValue(result, "result", "1")){
					this.dbManager.commitTransaction();
				}else{
					this.dbManager.rollback();
				}
			}else{
				result.put("result", "0");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			result.put("result", "3");
			result.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			request.setAttribute("deleteInfo", result);
		}
		return "delalert";
	}
	
	/**
	 * 使用ajax技术删除对应表中的数据
	 *
	 */
	public void deleteAjax(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		String author = (String) session.getAttribute("ID");
		Map<String, Object> result = new Hashtable<String, Object>();
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.dataModel = new DataManageModel(this.dbManager);
				this.dbManager.beginTransaction();
				result = this.dataModel.delete(strMap);
				if(!MapUtil.compareKeyValue(result, "result", "1")){
					this.dbManager.commitTransaction();
				}else{
					this.dbManager.rollback();
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			result.put("result", "3");
			result.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			if(out!=null){
				this.jsonObject = JSONObject.fromObject(result);
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 使用ajax技术删除对应表中的数据
	 *
	 */
	public void sqliteDeleteAjax(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		String author = (String) session.getAttribute("ID");
		Map<String, Object> result = new Hashtable<String, Object>();
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			
			this.dbManager = new DBManager();
			this.dataModel = new DataManageModel(this.dbManager);
			this.dbManager.beginTransaction();
			result = this.dataModel.sqliteDelete(strMap);
			result.put("success", true);
			if(!MapUtil.compareKeyValue(result, "result", "1")){
				this.dbManager.commitTransaction();
			}else{
				this.dbManager.rollback();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			result.put("success", false);
			result.put("result", "3");
			result.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			if(out!=null){
				this.jsonObject = JSONObject.fromObject(result);
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 通用查询语句
	 * @return
	 */
	public String read(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		try {
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			if(!MapUtil.checkMapParamsNotNULL(strMap, "url")){
				return INPUT;
			}
			this.url = HtmlUtils.htmlUnescape(strMap.get("URL"));
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.dataModel = new DataManageModel(this.dbManager);
				Map<String, Object> recordInfo = this.dataModel.read(strMap);
				request.setAttribute("recordInfo", recordInfo);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
		}
		return "read";
	}
	
	/**
	 * 使用ajax技术删除对应表中的数据
	 *
	 */
	public void existAjax(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		String author = (String) session.getAttribute("ID");
		Map<String, Object> result = new Hashtable<String, Object>();
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.dataModel = new DataManageModel(this.dbManager);
				result = this.dataModel.exist(strMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("result", "3");
			result.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			if(out!=null){
				this.jsonObject = JSONObject.fromObject(result);
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 使用ajax技术删除对应表中的数据
	 *
	 */
	public void existSqliteAjax(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		String author = (String) session.getAttribute("ID");
		Map<String, Object> result = new Hashtable<String, Object>();
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			
			this.dbManager = new DBManager();
			this.dataModel = new DataManageModel(this.dbManager);
			result = this.dataModel.existSqlite(strMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("result", "3");
			result.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			if(out!=null){
				this.jsonObject = JSONObject.fromObject(result);
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * Oracle提交操作，并将修改的信息输出到response中
	 *
	 */
	public void operate(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=utf-8");
		HttpSession session = request.getSession();
		String author = (String)session.getAttribute("ID");
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			Map<String, File> fileMap = RequestUtil.readRequestFileList(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				String path=ServletActionContext.getServletContext().getRealPath("/");
				strMap.put("path", path);
				this.dbManager = new OracleOfDBManager(IP, type);
				this.dataModel = new DataManageModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.dataModel.operate(strMap, fileMap);
				resultMap.put("success", true);
				if(!MapUtil.compareKeyValue(resultMap, "result", "1")){
					this.dbManager.commitTransaction();
				}else{
					this.dbManager.rollback();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			resultMap.put("success", false);
			resultMap.put("result", "2");
			resultMap.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * Oracle提交操作
	 * 		返回到临时页面
	 *
	 */
	public String operateSubmit(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String author = (String)session.getAttribute("ID");
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			Map<String, File> fileMap = RequestUtil.readRequestFileList(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				String path=ServletActionContext.getServletContext().getRealPath("/");//获得项目路径
				strMap.put("path", path);
				this.dbManager = new OracleOfDBManager(IP, type);
				this.dataModel = new DataManageModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.dataModel.operate(strMap, fileMap);
				if(!MapUtil.compareKeyValue(resultMap, "result", "1")){//如果信息修改成功
					this.dbManager.commitTransaction();
					request.setAttribute("record", resultMap.get("record"));//如果是插入操作，将保存到的主键ID放入request中
				}else{
					this.dbManager.rollback();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			resultMap.put("result", "2");
			resultMap.put("exception", e.getMessage());
		} finally {
			request.setAttribute("result", resultMap.get("result"));
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
		}
		return "editalert";
	}
	
	/**
	 * Sqlite数据库操作
	 *
	 */
	public void sqliteOperate(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		HttpSession session = request.getSession();
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			String author = (String)session.getAttribute("ID");
			strMap.put("AUTHOR", author);
			this.dbManager = new DBManager();
			this.dataModel = new DataManageModel(this.dbManager);
			this.dbManager.beginTransaction();
			resultMap = this.dataModel.sqliteOperate(strMap);
			resultMap.put("success", true);
			if(!MapUtil.compareKeyValue(resultMap, "result", "1")){//如果信息修改成功
				this.dbManager.commitTransaction();
			}else{
				this.dbManager.rollback();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			resultMap.put("success", false);
			resultMap.put("result", "2");
			resultMap.put("exception", e.getMessage());
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
