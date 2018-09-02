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
import com.shxy.www.model.GridDataModel;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;

public class GridDataAction extends ActionSupport {
	
	private static final long serialVersionUID = 892022305191499712L;
	private DBManager dbManager = null;
	private GridDataModel gridData = null;
	private JSONObject jsonObject = new JSONObject();

	/**
	 * 读取Oracle数据库中的数据
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
			String IP = (String)session.getAttribute("IP");//获得用户操作的数据库IP
			String type = (String)session.getAttribute("type");//获得用户操作的数据库类型
			if(!StringUtil.arrIsNULL(IP,type)){
				strMap.put("IP", IP);
				strMap.put("DBTYPE", type);
				this.dbManager = new OracleOfDBManager(IP, type);
				this.gridData = new GridDataModel(this.dbManager);
				
				Map<String, Object> result = this.gridData.gridData(strMap);//获得grid数据
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
			if(this.dbManager!=null){
				this.dbManager.close();
			}
			this.jsonObject = JSONObject.fromObject(resultMap);
			out.print(this.jsonObject.toString());
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 读取Sqlite数据库中的grid数据
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
			String author = (String)session.getAttribute("ID");//获得用户登录ID
			strMap.put("author", author);
			String IP = (String) session.getAttribute("IP");//获得用户操作的数据库IP
			strMap.put("IP", ObjectUtil.obj2Str(IP));
			boolean manage = (Boolean)session.getAttribute("manage");//获得用户类型（true=管理员 false=普通用户）
			strMap.put("manage", manage?"1":"0");
			this.dbManager = new DBManager();
			this.gridData = new GridDataModel(this.dbManager);
			
			Map<String, Object> result = this.gridData.sqliteGridData(strMap);//读取本地Sqlite数据库内容
			resultMap.put("success", true);
			resultMap.put("data", result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("exception", e.getMessage());
		}if(out!=null){
			if(this.dbManager!=null){
				this.dbManager.close();
			}
			this.jsonObject = JSONObject.fromObject(resultMap);
			out.print(this.jsonObject.toString());
			out.flush();
			out.close();
		}
	}
}
