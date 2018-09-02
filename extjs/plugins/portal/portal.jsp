<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.shxy.www.util.StringUtil"%>
<html>
<%
String url = request.getContextPath();

String IP = (String)session.getAttribute("IP");
String type = (String)session.getAttribute("type");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>项目情况</title>
<script type="text/javascript">
var IP = '<%=StringUtil.obj2Str(IP)%>';
var type = '<%=StringUtil.obj2Str(type)%>';
</script>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="<%=url %>/css/control.css" />
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/plugins/portal/portal.css" />
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/dateextend.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/portal/portal.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/portal/otheroperate.js"></script>
<script type="text/javascript">
    Ext.require([
        'Ext.layout.container.*',
        'Ext.resizer.Splitter','*'
    ]);
    
    var debugControl = null;

    Ext.onReady(function(){
        Ext.create('Ext.app.Portal');
        debugControl = Ext.create('Birt.debug.control');
    });
</script>
<style>
.x-grid-row-over .x-grid-cell-inner {
    font-weight: bold;
}
.conn {
    background-image:url(<%=url %>/extjs/plugins/shared/icons/fam/connect.gif) !important;
}
.add_db {
    background-image:url(<%=url %>/extjs/plugins/shared/icons/fam/add_database.png) !important;
}
.disconn {
    background-image:url(<%=url %>/extjs/plugins/shared/icons/fam/disconnect.png) !important;
}
.trans_type{background-image: url(<%=url %>/extjs/plugins/shared/icons/fam/trans_type.png);}
.trans_format{background-image: url(<%=url %>/extjs/plugins/shared/icons/fam/trans_format.png);}
.transtype{width:100%; height: 100%;}
.transformat{width:100%; height: 100%;}
</style>
</head>
<body>
	<span id="app-msg" style="display:none;"></span>
</body>
<iframe id="alert_tmp" name="alert_tmp" style="display: none;" src=""></iframe>
<form id="form1" name="form1" action="ReportConfig_saveFormat.action" target="alert_tmp" method="post" style="display: none">
	<input id="TAB_046_005" name="TAB_046_005">
	<input id="TAB_049_IDS" name="TAB_049_IDS">
	<input id="TAB_046_ID" name="TAB_046_ID">
</form>
</html>