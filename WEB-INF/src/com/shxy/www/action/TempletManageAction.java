package com.shxy.www.action;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.shxy.www.conf.DBManager;
import com.shxy.www.conf.OracleOfDBManager;
import com.shxy.www.model.TempletManageModel;
import com.shxy.www.util.MapUtil;
import com.shxy.www.util.ObjectUtil;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;

public class TempletManageAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7770469006522672943L;
	private DBManager dbManager = null;
	private TempletManageModel templetModel = null;
	
	public TempletManageAction(){
	}

	/**
	 * 添加模板
	 * @return
	 */
	public String addTemplet(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		try {
			Map<String, String> strMap = RequestUtil.readRequestOfStringForEscape(request);
			Map<String, File> fileMap = RequestUtil.readRequestFileList(request);
			String IP = (String) session.getAttribute("IP");
			String type = (String) session.getAttribute("type");
			if(!StringUtil.arrIsNULL(IP, type)){
				this.dbManager = new OracleOfDBManager(IP, type);
				this.templetModel = new TempletManageModel(this.dbManager);
				this.dbManager.beginTransaction();
				String path= ServletActionContext.getServletContext().getRealPath("/");
				strMap.put("PATH", path);
				Map<String, String> result = this.templetModel.addTemplet(strMap, fileMap);
				request.setAttribute("result", result.get("result"));
				if(!MapUtil.compareKeyValue(result, "result", "1")){
					this.dbManager.commitTransaction();
					request.setAttribute("record", result.get("record"));
				}else{
					this.dbManager.rollback();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.dbManager.rollback();
		} finally {
			if(!ObjectUtil.objIsNull(this.dbManager)){
				this.dbManager.close();
			}
		}
		return "editalert";
	}
}
