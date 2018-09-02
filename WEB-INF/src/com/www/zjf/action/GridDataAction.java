package com.www.zjf.action;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.shxy.www.conf.DBManager;
import com.www.zjf.model.GridDataModel;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;

public class GridDataAction extends ActionSupport {
	
	private static final long serialVersionUID = 892022305191499712L;
	private GridDataModel gridData = null;
	private JSONObject jsonObject = new JSONObject();
	private DBManager SQLiteManager = null;
	
	
	/**
	 * ��ȡOracle���ݿ��е�����
	 *
	 */
	public void readData(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		response.setContentType("text/jsonl;charset=utf-8");
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			this.SQLiteManager = new DBManager();
			String IP = (String)session.getAttribute("IP");
			String id = (String)strMap.get("ID");
			String type = (String)strMap.get("TYPE");
			IP="2";
			if(!StringUtil.arrIsNULL(IP,type)){
				strMap.put("IP", IP);
				strMap.put("type", type);
				this.gridData = new GridDataModel(SQLiteManager);
				Map<String, Object> result = this.gridData.gridData(strMap);
				resultMap.put("success", true);
				resultMap.put("data", result);
			}else{
				resultMap.put("success", false);
				resultMap.put("data", "");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("exception", e.getMessage());
		}if(out!=null){
			this.jsonObject = JSONObject.fromObject(resultMap);
			out.print(this.jsonObject.toString());
			out.flush();
			out.close();
		}
	}
	
	/**
	 * ��ȡSqlite���ݿ��е�grid����
	 *
	 */
	public void sqliteData(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/jsonl;charset=utf-8");
		PrintWriter out = null;
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		HttpSession session = request.getSession();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			String author = (String)session.getAttribute("ID");//����û���¼ID
			strMap.put("author", author);
			String IP = (String) session.getAttribute("IP");//����û����������ݿ�IP
			strMap.put("IP", ObjectUtil.obj2Str(IP));
			boolean manage = (Boolean)session.getAttribute("manage");//����û����ͣ�true=����Ա false=��ͨ�û���
			strMap.put("manage", manage?"1":"0");
			
			Map<String, Object> result = this.gridData.sqliteGridData(strMap);//��ȡ����Sqlite���ݿ�����
			resultMap.put("success", true);
			resultMap.put("data", result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("exception", e.getMessage());
		}if(out!=null){
			this.jsonObject = JSONObject.fromObject(resultMap);
			out.print(this.jsonObject.toString());
			out.flush();
			out.close();
		}
	}
}
