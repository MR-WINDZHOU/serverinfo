package com.shxy.www.login;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.shxy.www.conf.DBManager;
import com.shxy.www.conf.OracleOfDBManager;
import com.shxy.www.model.IPManageModel;
import com.shxy.www.util.ListUtil;
import com.shxy.www.util.RequestUtil;
import com.shxy.www.util.StringUtil;

/**
 * 用户操作特殊类
 * @author 藕旺江
 * 修改日期 2010-10-16
 */
public class LoginAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private DBManager dbManager = null;
	
	public LoginAction(){
		this.dbManager = new DBManager();
	}

	/**
	 * 用户登录验证
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void verify() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter out = null;
		try {
			Map<String, String> params = RequestUtil.readRequestOfStringForEscape(request);
			String sql = "SELECT ID,S001,S003,S004 FROM SYSTAB200 WHERE S001=param1 AND S002=param2".replaceAll("param1", StringUtil.strAddQuotes(params.get("USERNAME"))).replaceAll("param2", StringUtil.strAddQuotes(params.get("PASSWORD")));
			List<List> userInfo = this.dbManager.executeQueryList(sql);
			out = response.getWriter();
			if(!ListUtil.listIsNull(userInfo)){
				String address = RequestUtil.getIpAddress(request);
				List<Object> ht = userInfo.get(0);
				HttpSession session = request.getSession();
				session.setAttribute("ID", ht.get(0));
				session.setAttribute("loginName", ht.get(1));
				session.setAttribute("showName", ht.get(3));
				session.setAttribute("manage", StringUtil.compare2Obj(ht.get(2), "1"));
				session.setAttribute("address", address);
				Map<String, Object> connStatus = new IPManageModel(this.dbManager).connStatus(ht.get(0).toString());
				DBManager testDBManager = null;
				if(!connStatus.isEmpty()){
					try {
						session.setAttribute("dbUserName", connStatus.get("dbUserName"));
						session.setAttribute("dbPassword", connStatus.get("dbPassword"));
						testDBManager = new OracleOfDBManager(connStatus.get("IP")+"",connStatus.get("type")+"");
						testDBManager.executeQueryList("SELECT COUNT(1) FROM DUAL");
						session.setAttribute("IP", connStatus.get("IP"));
						session.setAttribute("type", connStatus.get("type"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
					} finally {
						if(testDBManager!=null){
							testDBManager.close();
						}
					}
				}
				
				ServletContext sc=ServletActionContext.getServletContext();
				Hashtable<String, Object> loginUsers = (Hashtable<String, Object>)sc.getAttribute("loginUsers");
				if(loginUsers!=null){
					if(loginUsers.containsKey(String.valueOf(ht.get(0)))){
						HttpSession loginSession=(HttpSession)loginUsers.get(String.valueOf(ht.get(0)));
						if(loginSession!=null&&!session.equals(loginSession)){
							loginUsers.remove(String.valueOf(ht.get(0)));
							try{
								loginSession.invalidate();
							}catch(Exception e){
								
							}
						}
					}else{
						loginUsers.put(String.valueOf(ht.get(0)), session);
						session.getServletContext().setAttribute("loginUsers",loginUsers);
					}
				}else{
					loginUsers = new Hashtable<String, Object>();
					loginUsers.put(String.valueOf(ht.get(0)), session);
					session.getServletContext().setAttribute("loginUsers",loginUsers);
				}
				
				out.print("1");
			} else {
				out.print("2");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(out!=null){
				out.print("2");
			}
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			this.dbManager.close();
		}
	}
	
	/**
	 * 退出系统
	 *
	 */
	public void logout(){
		HttpServletRequest request=ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		ServletContext sc=ServletActionContext.getServletContext();
		HttpSession session=request.getSession();
		Map<String, String> result = new HashMap<String, String>();
		PrintWriter out = null;
		try{
			out = response.getWriter();
			String userId=(String)session.getAttribute("ID");
			Hashtable allUser=(Hashtable)sc.getAttribute("loginUsers");
			if(StringUtil.arrIsNULL(userId)){
				result.put("result", "1");
			}else{
				if(allUser!=null){
					allUser.remove(userId);
				}
				session.invalidate();
				result.put("result", "1");
			}
		}catch(Exception e){
			e.printStackTrace();
			result.put("result", "2");
			result.put("exception", e.getMessage());
		}finally{
			this.dbManager.close();
			if(out!=null){
				JSONObject jsonObj = JSONObject.fromObject(result);
				out.print(jsonObj.toString());
				out.flush();
				out.close();
			}
		}
	}
}
