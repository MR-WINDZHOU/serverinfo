<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.shxy.www.util.StringUtil"%>
<html>
<%
String url = request.getContextPath();
String configId = request.getParameter("configId");
String edit = request.getParameter("edit");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>模板管理</title>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<script type="text/javascript">
var configRecord = '<%=configId %>';
var edit = Boolean(<%=StringUtil.arrIsNULL(edit)?0:edit %>);
</script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/dateextend.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/layout/filterconf/filterconf.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<style>
.save{background-image: url(<%=url %>/extjs/plugins/shared/icons/fam/save.png)}
form{width:100%; height: 100%; padding: 3px; padding-bottom: 6px;}
textarea{width:100%; height: 100%;}
</style>
</head>
<body>
</body>
<iframe id="alert_tmp" name="alert_tmp" style="display: none;" src=""></iframe>
</html>