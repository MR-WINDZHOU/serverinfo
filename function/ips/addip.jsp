<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
String url = request.getContextPath();
%>
<head>
<title>添加用户</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="<%=url %>/css/control.css"/>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/iframeobj.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/ajaxextend.js"></script>
<script type="text/javascript">
$(function(){
	$("#save").click(function(){
		if(checkIP($("#S001"))){
			return;
		}
		if(saveSubmitAsync("<%=url %>/IPManage_addIP.action", "S001="+$("#S001").val()+"&S002="+$("#S002").val()+"&S003="+$("#S003").val()+"&S004="+$("#S004").val())){
			parent.IPListStore.load();
			parent.dialogWindow.hide();
			document.location.reload();
		}
	});
});

function checkIP(th){
	var pattern=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
	if(!pattern.test($(th).val()))
	{
		alert("IP地址输入错误，请重新输入!");
		$(th).focus();
		return true;
	}
	return false;
}
</script>
</head>
<body>
	<table class="accordion">
		<tr height="28">
			<td align="right" width="40%">数据库IP地址</td>
			<td align="left"><input id="S001" name="S001" maxlength="15" class="text" onchange="checkIP(this)"/></td>
		</tr>
		<tr height="28">
			<td align="right">数据库类型</td>
			<td align="left">
				<select id="S002" name="S002">
					<option value="0">基层</option>
					<option value="1">机关</option>
				</select>
			</td>
		</tr>
		<tr height="28">
			<td align="right">数据库用户名</td>
			<td align="left">
				<input id="S003" name="S003">
			</td>
		</tr>
		<tr height="28">
			<td align="right" width="40%">数据库密码</td>
			<td align="left"><input id="S004" name="S004" maxlength="30" class="password"  value='shanghaixinyuan'/></td>
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
<iframe id="alert" name="alert" style="display:none;"></iframe>
</body>
</html>