package com.www.zjf.action;

import java.io.File;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.springframework.web.util.HtmlUtils;

import com.opensymphony.xwork2.ActionSupport;
import com.shxy.www.conf.DBManager;
import com.shxy.www.conf.OracleOfDBManager;
import com.www.zjf.model.DataManageModel;
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
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * 以ajax的形式保存提交的内容到Sqlite数据库中
	 * 
	 */
	public void saveSerDetail(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/json");
		PrintWriter out = null;
		String result = "0";
		Map<String, String> resultMap = new Hashtable<String, String>();
		try {
			out = response.getWriter();
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);//获得用户提交的参数信息
			JSONObject info = JSONObject.fromObject((String)request.getParameter("params"));
			this.dbManager = new DBManager();
			this.dataModel = new DataManageModel(this.dbManager);
			this.dbManager.beginTransaction();
			result = this.dataModel.saveSqliteInfo(info);//保存信息到Sqlite数据库中
			if("1".equals(result)){//如果保存成功
				this.dbManager.commitTransaction();
				resultMap.put("result", "1");
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
}
