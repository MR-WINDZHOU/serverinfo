package com.shxy.www.action;

import java.io.PrintWriter;
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
import com.shxy.www.model.ReportConfigModel;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;

public class ReportConfigAction extends ActionSupport {

	private static final long serialVersionUID = -7147026903334651803L;
	private DBManager dbManager = null;
	private ReportConfigModel reportModel = null;
	private JSONObject jsonObject = new JSONObject();
	
	/**
	 * 字段转换格式设置
	 *
	 */
	public void operate(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		HttpSession session = request.getSession();
		String author = (String)session.getAttribute("ID");
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.reportModel = new ReportConfigModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.reportModel.operate(strMap);
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
	 * 修改字段排序
	 */
	public void changeOrders(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		String author = (String)session.getAttribute("ID");
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.reportModel = new ReportConfigModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.reportModel.changeOrders(strMap);
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
	 * 向报表模板中插入字段
	 */
	public void insertField(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		HttpSession session = request.getSession();
		String author = (String)session.getAttribute("ID");
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.reportModel = new ReportConfigModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.reportModel.insertField(strMap);
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
	 * 保存转换格式内容
	 */
	public String saveFormat(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String author = (String)session.getAttribute("ID");
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.reportModel = new ReportConfigModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.reportModel.saveFormat(strMap);
				if(!MapUtil.compareKeyValue(resultMap, "result", "1")){
					this.dbManager.commitTransaction();
				}else{
					this.dbManager.rollback();
				}
				request.setAttribute("result", MapUtil.get(resultMap, "result"));
			}else{
				request.setAttribute("result", "2");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
			request.setAttribute("result", "2");
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
		}
		return "alert";
	}
	
	/**
	 * 字段转换格式设置
	 *
	 */
	public void fieldFormat(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		String author = (String)session.getAttribute("ID");
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			strMap.put("AUTHOR", author);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.reportModel = new ReportConfigModel(this.dbManager);
				this.dbManager.beginTransaction();
				resultMap = this.reportModel.fieldFormat(strMap);
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
	
}
