<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
String url = request.getContextPath();

boolean manage = (Boolean)session.getAttribute("manage");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>字段详细转换格式配置</title>
<script type="text/javascript">
var manage = Boolean(<%=manage %>);
</script>
<link rel="stylesheet" type="text/css" href="<%=url %>/extjs/resources/css/ext-all.css" />
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/bootstrap.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/dateextend.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/plugins/layout/editurl/editurl.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/extjs/locale/ext-lang-zh_CN.js"></script>
<style type="text/css">
.addurl{background-image: url(<%=url%>/extjs/plugins/shared/icons/fam/addurl.png)}
.delurl{background-image: url(<%=url%>/extjs/plugins/shared/icons/fam/delurl.png)}
.addproject{background-image: url(<%=url%>/extjs/plugins/shared/icons/fam/server_add.png)}
.export_document{background-image: url(<%=url%>/extjs/plugins/shared/icons/fam/export-document.png)}
.zoom{background-image: url(<%=url%>/extjs/plugins/shared/icons/fam/zoom.png)}
.update{background-image: url(<%=url%>/extjs/plugins/shared/icons/fam/archive_extract.png)}
</style>
</head>
<body>
</body>
<iframe id="alert_tmp" name="alert_tmp" style="display: none;" src=""></iframe>
</html>