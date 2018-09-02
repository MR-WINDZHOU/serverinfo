<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.shxy.www.util.ObjectUtil"%>
<html>
<%
response.setHeader("Pragma","No-cache");//HTTP 1.1 
response.setHeader("Cache-Control","no-cache");//HTTP 1.0 
response.setHeader("Expires","0");//防止被proxy 
String url = request.getContextPath();
Object result = request.getAttribute("result");//执行记过
Object record = request.getAttribute("record");//执行插入操作时， 数据库主键值
String id = request.getParameter("id");//更新时，主键ID
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script language="javascript" src="<%=url %>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=url %>/js/iframeobj.js" type="text/javascript"></script>
<title>保存页面信息 弹窗结果页面</title>
</head>
<body>
<script type="text/javascript">
<%if(result==null||result.equals("")){%>
	alert("数据库错误！");
<%}else if(result.equals("0")){%>
	alert("参数错误！");
<%}else if(result.equals("1")){%>
	//alert("信息保存成功！");
	<%if(!ObjectUtil.arrIsNULL(id)||!ObjectUtil.arrIsNULL(record)){
		if(ObjectUtil.arrIsNULL(record)){
			record=id;
		}%>
	if(typeof(parent.reloadIframe)=='function'){
		parent.reloadIframe('<%=ObjectUtil.arrIsNULL(record)?"":record %>');
	}
	<%}%>
	/**
	 * 关闭弹窗
	 */
	if(typeof(parent.closePage)=='function'){
		parent.closePage();
	}
<%}else if(result.equals("2")){%>
	alert("信息保存失败！");
<%}else if(result.equals("3")){%>
<%}else if(result.equals("4")){%>
<%}else if(result.equals("5")){%>
<%}else if(result.equals("6")){%>
<%}%>
</script>
</body>
</html>