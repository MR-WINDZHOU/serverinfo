<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
String url = request.getContextPath();
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>欢迎页</title>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<style>
html, body{width: 100%; height: 100%; padding: 0px; margin: 0px;}
</style>
<script type="text/javascript">
Ext.require(['Ext.Viewport']);

Ext.onReady(function() {
    
    Ext.create('Ext.Viewport', {
        layout:'anchor',
        items:[{
            title:'编辑区域',
         	html:'<iframe id="editarea" name="editarea" src="'+url+'/welcome.jsp" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>',
            anchor:'100% 100%'
        }]
    });
});
</script>
</head>
<body>
</body>
</html>