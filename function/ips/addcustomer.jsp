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
<script language="javascript" type="text/javascript" src="<%=url %>/js/operateDetails.js"></script>
<script type="text/javascript">
function checkIfNull(){
	var noticeFlag = 0;
	$("input[class='inputNotnull']").each(function(index,obj){
		if($(obj).val()==null||$(obj).val()==""){
			noticeFlag = 1 ;
		}
	});
	if(noticeFlag == 1){
		alert("请将表格填写完整！");
	}
	return noticeFlag;
}
$(function(){
	var params ={};
	var xuhao = "";
	var customername = "";
	var status = "";
	var addnote = "";
	$("#save").click(function(){
		if(checkIfNull()!=1){
			xuhao = $("#xuhao").val();
			customername = $("#customername").val();
			status = $("#status").val();
			addnote = $("#addnote").val();
			params ={
					"xuhao":xuhao,
					"customername":customername,
					"status":status,
					"addnote":addnote
			}
			saveClientInfo(params);
		}
		
	});
});


</script>
</head>
<body>
	<table  class="accordion">
		<tr height="28">
			<td align="right" width="30%">序号</td>
			<td align="left"><input id="xuhao"  class="inputNotnull" /></td>
		</tr>
		<tr height="28">
			<td align="right">客户名称</td>
			<td align="left"><input id="customername"  class="inputNotnull"/></td>
		</tr>
		<tr height="28">
			<td align="right">状态</td>
			<td align="left">
				<select id="status" >
					<option value="0">启动</option>
					<option value="1">运行</option>
					<option value="2">暂停</option>
				</select>
			</td>
		</tr>
		<tr height="28">
			<td align="right" width="30%">备注</td>
			<td align="left"><input id="addnote" class="inputNotnull"  /></td>
		</tr>
		<!-- <tr>
			<td align="center" colspan="2">&nbsp;</td>
		</tr> -->
		<tr>
			<td align="center" colspan="3" height="38">
				<input type="button" class="input2" id="save" value="保存"/>
			</td>
		</tr>
	</table>
<iframe id="alert" name="alert" style="display:none;"></iframe>
</body>
</html>