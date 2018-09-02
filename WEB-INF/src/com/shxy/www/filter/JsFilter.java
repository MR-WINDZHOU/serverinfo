package com.shxy.www.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 为每个页面添加一个js变量_$realpath='项目名称'
 * @author shxy
 *
 */
public class JsFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hReq = (HttpServletRequest) req;
		HttpServletResponse hRes = (HttpServletResponse) res;
		ResponseWrapper crw = new ResponseWrapper(hRes);
		chain.doFilter(req, crw);
		String content = crw.toString();
		boolean temp=false;
		int position =content.indexOf("X-UA-Compatible");
		//含有IE=EmulateIE7
		if(position!=-1){
			String tempStr=content.substring(position,content.length());
			int tempIndex=-1;
			int temp1=tempStr.indexOf(">");
			int temp2=tempStr.indexOf("/>");
			if(temp1<=temp2){
				tempIndex=temp1+1;
			}else{
				tempIndex=temp2+2;
			}
			position+=tempIndex;
			temp=true;
		}else{//不含IE=EmulateIE7
			position=content.indexOf("<html>");
			if(position==-1){
				position =content.indexOf("<head>");
				if(position!=-1){
					temp=true;
				}
			}else{
				temp=true;
			}
		}
		if(temp&&position<=content.length()){
			String beforeContent=content.substring(0, position);
			String afterContent=content.substring(position,content.length());
			beforeContent+="<script type=\"text/javascript\">var url='"+hReq.getContextPath()+"';</script>";
			content=beforeContent+afterContent;
		}
		try {
			PrintWriter pw = hRes.getWriter();
			pw.println(content);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			
		}
	}

	public void init(FilterConfig fc) throws ServletException {

	}

}

