<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
response.setHeader("Pragma","No-cache");//HTTP 1.1 
response.setHeader("Cache-Control","no-cache");//HTTP 1.0 
response.setHeader("Expires","0");//防止被proxy 
String url = request.getContextPath();
String result = (String)request.getAttribute("result");
String recordId = (String)request.getAttribute("recordId");
String id = request.getParameter("id");
String field = request.getParameter("field");
String fieldName = request.getParameter("fieldName");
String fieldValue = request.getParameter(field);
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script language="javascript" src="<%=url %>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=url %>/js/iframeobj.js" type="text/javascript"></script>
<title>Insert title here</title>
</head>
<body>
<script type="text/javascript">
<%if(result==null||result.equals("")){%>
	alert("数据库错误！");
<%}else if(result.equals("0")){%>
	alert("参数错误！");
<%}else if(result.equals("1")){%>
	alert("信息保存成功！");
	<%if((id!=null&&!id.equals(""))||(recordId!=null&&!recordId.equals(""))){if(id!=null&&!id.equals("")){recordId=id;}%>
	if(typeof(parent.reloadIframe)=='function'){
		parent.reloadIframe('<%=recordId==null||recordId.equals("")?"":recordId%>');
	}
	<%}%>
	/**
	 * 关闭弹窗
	 */
	if(typeof(parent.closeDialog)=='function'){
		parent.closeDialog();
	}
<%}else if(result.equals("2")){%>
	alert("信息保存失败！");
<%}else if(result.equals("3")){%>
	alert("<%=fieldName %>=<%=fieldValue %> 已经使用, 请使用其他内容！");
	parent.$("#<%=field %>").focus();
<%}%>
</script>
</body>
</html>