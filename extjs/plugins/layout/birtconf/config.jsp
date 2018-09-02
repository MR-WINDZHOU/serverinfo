<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
String url = request.getContextPath();

boolean manage = (Boolean)session.getAttribute("manage");
String companyid = request.getParameter("companyid");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>服务器信息管理</title>
<script type="text/javascript">
var manage = Boolean(<%=manage %>);
var companyid = <%=companyid %>
</script>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/dateextend.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/layout/birtconf/config.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<style type="text/css">
.transtype{width:100%; height: 100%;}
.formatedit{cursor:pointer;width: 16px; height:16px;}

.save{background-image: url(<%=url %>/extjs/plugins/shared/icons/fam/save.png)}
.clear{background-image: url(<%=url %>/extjs/plugins/shared/icons/fam/clear.png)}
.refresh{background-image: url(<%=url%>/extjs/plugins/shared/icons/menu/reload.png)}
</style>
</head>
<body>
</body>
<iframe id="alert_tmp" name="alert_tmp" style="display: none;" src=""></iframe>
<form action="webReport" id="form2" name="form2" method="post" target="_blank" style="display: none;">
	<input type="hidden" name="flag" id="flag" />
	<input type="hidden" name="birtParam" id="birtParam"/>
	<input type="hidden" name="ReportName" id="ReportName" />
</form>
<form id="form1" name="form1" action="ReportConfig_saveFormat.action" target="alert_tmp" method="post" style="display: none">
	<input id="TAB_046_005" name="TAB_046_005">
	<input id="TAB_049_IDS" name="TAB_049_IDS">
	<input id="TAB_046_ID" name="TAB_046_ID">
</form>
</html>