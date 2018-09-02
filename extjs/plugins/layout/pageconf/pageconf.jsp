<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
String url = request.getContextPath();

boolean manage = (Boolean)session.getAttribute("manage");

String pageUrl = request.getParameter("pageUrl");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>模板管理</title>
<script type="text/javascript">
var manage = Boolean(<%=manage %>);

var pageUrl = "<%=pageUrl %>";
</script>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/layout/pageconf/pageconf.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<style>
.calendar_icon{background:url(<%=url %>/extjs/plugins/shared/icons/fam/calendar-blue.png)}
.script_icon{background:url(<%=url %>/extjs/plugins/shared/icons/fam/script_code.png)}
.export_xml{background:url(<%=url %>/extjs/plugins/shared/icons/fam/application_vnd.sun.xml.calc.png)}
.save{background:url(<%=url %>/extjs/plugins/shared/icons/fam/save.png)}
.edit_multi_table{background:url(<%=url %>/extjs/plugins/shared/icons/fam/database_edit.png)}
</style>
</head>
<body>
</body>
<iframe id="alert_tmp" name="alert_tmp" style="display: none;" src=""></iframe>
</html>