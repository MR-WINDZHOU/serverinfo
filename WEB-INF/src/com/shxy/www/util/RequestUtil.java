package com.shxy.www.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.web.util.HtmlUtils;

public class RequestUtil {
	
	/**
	 * 获得request中所有的页面提交的参数信息
	 * @param request
	 * @return
	 * 		Map[
	 * 				控件名	: 值
	 * 				……
	 * 			]
	 * @throws Exception
	 */
	public static Map<String, String> readRequestOfStringForEscape(HttpServletRequest request) throws Exception{
		Map<String, String> ht = new Hashtable<String, String>();
		Map<String, String[]> map = request.getParameterMap();
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){//循环遍历
			String key = it.next();//参数名
			String[] value = map.get(key);//参数值
			ht.put(key.toUpperCase(), StringUtil.arrary2String(value));
		}
		if(request instanceof MultiPartRequestWrapper){//如果request是文件流
			MultiPartRequestWrapper multi = (MultiPartRequestWrapper) request;
			Enumeration fileList = multi.getFileParameterNames();//获得文件列表信息
			while(fileList.hasMoreElements()){
				String controlName = (String) fileList.nextElement();//获得文件提交的控件名
				File[] file = multi.getFiles(controlName);//获得文件列表
				if(file.length>0){//存在上传的文件
					String[] filename = multi.getFileNames(controlName);//获得文件名
					ht.put(controlName.toUpperCase(), HtmlUtils.htmlEscape(filename[0]));
				}
			}
		}
		return ht;
	}
	
	
	/**
	 * 获得request中提交的文件信息
	 * @param request
	 * @return
	 * 		Map[
	 * 				控件名	: 上传的文件对象
	 * 			]
	 * @throws Exception
	 */
	public static Map<String, File> readRequestFileList(HttpServletRequest request) throws Exception{
		Map<String, File> ht = new Hashtable<String, File>();
		if(request instanceof MultiPartRequestWrapper){//如果request是文件流
			MultiPartRequestWrapper multi = (MultiPartRequestWrapper) request;
			Enumeration fileList = multi.getFileParameterNames();
			while(fileList.hasMoreElements()){
				String controlName = (String) fileList.nextElement();//获得参数名
				File[] file = multi.getFiles(controlName);//获得文件列表
				if(file.length>0){//如果存在提交的文件
					ht.put(controlName.toUpperCase(), file[0]);//将提交的文件放入Map中
				}
			}
		}
		return ht;
	}
	
	/**
	 * 获得客户端的IP地址
	 * @param request request对象
	 * @return
	 * 		返回用户登录的IP
	 */
	public static String getIpAddress(HttpServletRequest request) {
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}
}
