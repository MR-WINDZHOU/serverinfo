<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="java.util.Map"%>
<%@page import="com.shxy.www.util.MapUtil"%>
<%@page import="com.shxy.www.util.StringUtil"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<%
String url = request.getContextPath();
Map<String, Object> recordInfo = (Map<String, Object>) request.getAttribute("recordInfo");

String id = request.getParameter("id");
String parent = request.getParameter("parent");
%>
<head>
<title>网站模式选择</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="<%=url %>/css/control.css"/>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<script type="text/javascript">
$(function(){
	$("#save").click(function(){
		if($("#TAB_008_001").val()==""){
			alert("请填写组名称！");
			$("#TAB_008_001").focus();
			return;
		}
		$("#form1").attr("action", url+"/DataManage_saveSubmitInfo.action");
		$("#form1").submit();
	});
});

function reloadIframe(id){
	document.location = url+"/DataManage_read.action?url=/function/templet/groupmanage.jsp&table=TAB_4001&id="+id;
	parent.reloadNode();
}
</script>
</head>
<body>
<form id="form1" name="form1" target="alert" method="post" enctype="multipart/form-data">
	<table class="accordion">
		<tr height="28">
			<td align="right" width="40%">组名称</td>
			<td align="left"><input id="TAB_008_001" name="TAB_008_001" maxlength="100" class="text" value="<%=MapUtil.get(recordInfo, "TAB_008_001") %>"/></td>
		</tr>
		<tr>
			<td align="center" colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td align="center" colspan="3" height="28">
				<input type="button" class="input2" id="save" value="保存"/>
			</td>
		</tr>
	</table>
	<input type="hidden" name="table" value="TAB_4001"/>
	<%if(StringUtil.arrIsNULL(id)){ %>
	<input type="hidden" name="TAB_008_002" value="<%=parent %>"/>
	<%}else{ %>
	<input type="hidden" name="id" value="<%=id %>"/>
	<%} %>
</form>
<iframe id="alert" name="alert" style="display:none;"></iframe>
</body>
</html>