<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
String url = request.getContextPath();
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>模板管理</title>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/layout/templet/templetmanage.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
</head>
<body>
</body>
<iframe id="alert_tmp" name="alert_tmp" style="display: none;" src=""></iframe>
</html>