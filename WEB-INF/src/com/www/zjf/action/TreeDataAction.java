package com.www.zjf.action;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.shxy.www.conf.DBManager;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;
import com.www.zjf.model.TreeDataModel;

public class TreeDataAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DBManager dbManager = null;
	private TreeDataModel treeModel = null;
	private JSONArray jsonArray = new JSONArray();
	
	/**
	 * 获得Oracle数据库中tree信息
	 *
	 */
	public void tree(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json;charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			//if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new DBManager();
				this.treeModel = new TreeDataModel(this.dbManager);
				strMap.put("type","2");
				List<Map<String, Object>> jsonList = this.treeModel.tree(strMap);
				this.jsonArray = JSONArray.fromObject(jsonList);
			//}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(this.dbManager!=null){
				this.dbManager.close();
			}
			if(out!=null){
				out.print(this.jsonArray.toString());
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 获得本地数据库文件tree信息
	 *
	 */
	public void sqliteTree(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json;charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			if(!StringUtil.arrIsNULL(IP, type)){
				this.treeModel = new TreeDataModel(this.dbManager);
				
				List<Map<String, Object>> jsonList = this.treeModel.sqliteTree(strMap);
				this.jsonArray = JSONArray.fromObject(jsonList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(this.dbManager!=null){
				this.dbManager.close();
			}
			if(out!=null){
				out.print(this.jsonArray.toString());
				out.flush();
				out.close();
			}
		}
	}
}
