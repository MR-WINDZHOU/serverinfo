<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="java.util.Map"%>
<%@page import="com.shxy.www.util.MapUtil"%>
<%@page import="com.shxy.www.util.StringUtil"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<%
String url = request.getContextPath();
Map<String, Object> recordInfo = (Map<String, Object>)request.getAttribute("recordInfo");
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
	//document.getElementById("ISSYNCHRONOUS")[<%=MapUtil.get(recordInfo, "ISSYNCHRONOUS") %>-1].selected=true;
	//document.getElementById("IS_EXCEL")[<%=MapUtil.get(recordInfo, "IS_EXCEL") %>-1].selected=true;
	$("#save").click(function(){
		if($("#TAB_010_002").val()==""){
			alert("请填写模板名！");
			$("#TAB_010_002").focus();
			return ;
		}
		<%if(!MapUtil.mapIsNull(recordInfo)){%>
		if($("#TAB_010_001").val()==""){
			alert("请选择模板文件！");
			$("#TAB_010_001").focus();
			return ;
		}
		<%} %>
		$("#form1").attr("action", url+"/TempletManage_addTemplet.action");
		$("#form1").submit();
	});
});

function reloadIframe(id,recordInfo){
	parent.reloadNode();
	document.location = "<%=url %>/DataManage_read.action?url=/function/templet/templetmanage.jsp&table=TAB_4003&id="+id;
}

/**
 * 检查模板文件是否存在
 */
function existTemplet(obj){
	var val = obj.value;
	if(val==""){
		return false;
	}
	val = val.substring(val.lastIndexOf('\\')+1);
	if(val.lastIndexOf('.rptdesign')<0){
		alert("请选择正确的模板文件！");
		var obj1 = obj;
		var obj2 = obj1.cloneNode();
		obj1.parentNode.replaceChild(obj2,obj1);
		$("#TAB_010_001").focus();
		return true;
	}
	<%if(MapUtil.mapIsNull(recordInfo)){%>
	var _$records = existNoAlert("TAB_4003", "condition1=TAB_010_001&conditionValue1="+val);
	if(parseInt(_$records)>0){
		alert("模板 【"+val+"】 已使用，请重新选择！");
		var obj1 = obj;
		var obj2 = obj1.cloneNode();
		obj1.parentNode.replaceChild(obj2,obj1);
		$("#TAB_010_001").focus();
		return true;
	}
	<%}%>
	return false;
}
</script>
</head>
<body>
<form id="form1" name="form1" target="alert" method="post" enctype="multipart/form-data">
	<table class="accordion">
		<tr height="28">
			<td align="right" width="40%">模板名称</td>
			<td align="left"><input id="TAB_010_002" name="TAB_010_002" class="text" value="<%=MapUtil.get(recordInfo, "TAB_010_002") %>"/></td>
		</tr>
		<%if(!StringUtil.arrIsNULL(id)){ %>
		<tr height="28">
			<td align="right" width="40%">模板文件名</td>
			<td align="left"><input id="TAB_010_001_NAME" name="TAB_010_001_NAME" class="text" readonly="readonly" style="color: gray;" value="<%=MapUtil.get(recordInfo, "TAB_010_001") %>"/></td>
		</tr>
		<%} %>
		<tr height="28">
			<td align="right" width="40%">模板文件</td>
			<td align="left"><input id="TAB_010_001" name="TAB_010_001" type="file" title="<%= StringUtil.arrIsNULL(id)?"请选择正确的报表配置":"此时选择报表文件，将会替换原有的文件" %>" onchange="existTemplet(this)" class="text"/></td>
		</tr>
		<tr height="28">
		    <td align="right" width="40%">排序</td>
		    <td align="left" >
			    <select id="ISSYNCHRONOUS" name="ISSYNCHRONOUS" style="width:230px;">
				    <option value="1">1</option>
				    <option value="2">2</option>
				    <option value="3">3</option>
				    <option value="4">4</option>
				    <option value="5">5</option>
				    <option value="6">6</option>
				    <option value="7">7</option>
				    <option value="8">8</option>
				    <option value="9">9</option>
			    </select>
		    </td>
		</tr>
		<tr height="28">
			<td align="right" width="40%">是否可以导出EXCEL</td>
		    <td align="left">
		        <select id="IS_EXCEL" name="IS_EXCEL" style="width:230px;">
		        <option value="1">是</option>
		        <option value="0">否</option>
		        </select>
		    </td>
		
		
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
	<input type="hidden" name="table" value="TAB_4003"/>
	<%if(StringUtil.arrIsNULL(id)){ %>
	<input type="hidden" name="tab_011_001" value="<%=parent %>"/>
	<%}else{ %>
	<input type="hidden" name="id" value="<%=id %>"/>
	<%} %>
</form>
<iframe id="alert" name="alert" style="display:none;"></iframe>
</body>
</html>