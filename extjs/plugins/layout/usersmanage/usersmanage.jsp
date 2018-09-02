<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.shxy.www.util.StringUtil"%>
<html>
<%
String url = request.getContextPath();

boolean manage = (Boolean)session.getAttribute("manage");
String author = (String) session.getAttribute("ID");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>模板管理</title>
<script type="text/javascript">
var manage = Boolean(<%=StringUtil.arrIsNULL(manage)?0:manage %>);
var author = "<%=author %>";
</script>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/dateextend.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/layout/usersmanage/usersmanage.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<style>
.adduser{background-image: url(<%=url %>/extjs/plugins/shared/icons/fam/user_add.png);}
.save{background-image: url(<%=url %>/extjs/plugins/shared/icons/fam/save.png)}
</style>
</head>
<body>
</body>
<iframe id="alert_tmp" name="alert_tmp" style="display: none;" src=""></iframe>
</html>