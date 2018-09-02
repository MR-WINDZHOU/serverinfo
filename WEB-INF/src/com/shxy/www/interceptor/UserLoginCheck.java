package com.shxy.www.interceptor;

import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class UserLoginCheck extends AbstractInterceptor {

	private static final long serialVersionUID = -740106586199968939L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext ac = invocation.getInvocationContext();
		Map session = (Map) ac.get(ServletActionContext.SESSION);
		if (session != null) {
			if (session.get("ID") != null) {
				return invocation.invoke();
			}
		}
		return "relogin";
	}

}
