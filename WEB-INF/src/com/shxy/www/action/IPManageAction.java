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
import com.shxy.www.model.IPManageModel;

/**
 * 数据库IP管理类
 * @author 藕旺江
 *
 */
public class IPManageAction extends ActionSupport{
	
	private static final long serialVersionUID = 8600205170154942688L;
	private DBManager dbManager = null;
	private IPManageModel ipManage = null;
	private JSONObject jsonObject = new JSONObject();
	
	public IPManageAction() {
		this.dbManager = new DBManager();
		this.ipManage = new IPManageModel(this.dbManager);
	}
	
	/**
	 * 断开数据库连接
	 *
	 */
	public void disConn(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json;utf-8");
		String type = request.getParameter("type");
		HttpSession session = request.getSession();
		String author = (String)session.getAttribute("ID");
		PrintWriter out = null;
		Map<String, String> resultMap = new Hashtable<String, String>();
		try {
			out = response.getWriter();
			String result = this.ipManage.disConn(author, type);
			//移除session中的数据库IP和数据库类型
			session.removeAttribute("IP");
			session.removeAttribute("type");
			
			resultMap.put("result", result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.dbManager.close();
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 连接选定的数据库
	 *
	 */
	public void conn(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json;utf-8");
		String id = request.getParameter("id");
		HttpSession session = request.getSession();
		String author = (String)session.getAttribute("ID");
		PrintWriter out = null;
		Map<String, String> resultMap = new Hashtable<String, String>();
		try {
			out = response.getWriter();
			String result = this.ipManage.conn(author, id);
			
			Map<String, Object> connStatus = new IPManageModel(this.dbManager).connStatus(author);
			DBManager testDBManager = null;
			if(!connStatus.isEmpty()){
				try {
					//testDBManager = new OracleOfDBManager(connStatus.get("IP")+"",connStatus.get("type")+"",connStatus.get("dbUserName"),connStatus.get("dbPassword"));
					/**********20140514修改********************/
					session.setAttribute("dbUserName", connStatus.get("dbUserName"));
					session.setAttribute("dbPassword", connStatus.get("dbPassword"));
					
					testDBManager = new OracleOfDBManager(connStatus.get("IP")+"",connStatus.get("type")+"");
					testDBManager.executeQueryList("SELECT COUNT(1) FROM DUAL");
					session.setAttribute("IP", connStatus.get("IP"));
					session.setAttribute("type", connStatus.get("type"));
					//session.setAttribute("dbUserName", connStatus.get("dbUserName"));
					//session.setAttribute("dbPassword", connStatus.get("dbPassword"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					result = "-1";
				} finally {
					if(testDBManager!=null){
						testDBManager.close();
					}
				}
			}
			resultMap.put("result", result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.dbManager.close();
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 添加数据库IP
	 *
	 */
	public void addIP(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json;utf-8");
		String S001 = request.getParameter("S001");
		String S002 = request.getParameter("S002");
		/*****************20140514修改，添加S003、S004***********************/
		String S003 = request.getParameter("S003");
		String S004 = request.getParameter("S004");
		/*****************20140514修改，添加S003、S004***********************/
		PrintWriter out = null;
		Map<String, String> resultMap = new Hashtable<String, String>();
		try {
			out = response.getWriter();
			String result = this.ipManage.addIP(S001, S002,S003, S004);
			resultMap.put("result", result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.dbManager.close();
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
		
	}
	
	
	public void deleteIP(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json;utf-8");
		String id = request.getParameter("ids");
		PrintWriter out = null;
		Map<String, String> resultMap = new Hashtable<String, String>();
		try {
			out = response.getWriter();
			String result = this.ipManage.deleteIP(id);
			resultMap.put("result", result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			this.dbManager.close();
			this.jsonObject = JSONObject.fromObject(resultMap);
			if(out!=null){
				out.print(this.jsonObject.toString());
				out.flush();
				out.close();
			}
		}
	}
}
