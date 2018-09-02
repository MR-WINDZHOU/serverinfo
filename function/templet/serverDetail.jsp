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
<script language="javascript" type="text/javascript" src="<%=url %>/js/operateDetails.js"></script>
<script type="text/javascript">
function saveDetailtable(){
	var colname = "";
	var datavalue = "";
	var itemid = "";
	var changedColsArray = [];
	$("#detailId input[changed='1']").each(function(index,obj){
		itemid = $(obj).attr("itemid");
		colname = $(obj).attr("colname");
		datavalue = $(obj).val();
		var arr  =
	     {
	         "itemid" : itemid,
	         "colname" : colname,
	         "datavalue" : datavalue
	     }
		changedColsArray.push(arr);
	});
	console.log(changedColsArray);
	var changedColsJson = {"changedColsJson":changedColsArray};
	/* var columnInfos ={
  		  "userName" : userName,
			  "password" : password				  
	}; */
	//$.toJSONString(
	/* for (){
		
	} */
	$.ajax({
		type: "POST",
		url: url+"/DataManageZJF_saveSerDetail.action",
		data: "TYPE=4"+"&params="+JSON.stringify(changedColsJson),
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			updateDetailTable(<%=id %>);
			return true; 
		}
	});
}
$(function(){
	var id = "<%=id %>";
	updateDetailTable(id);
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
<form id="form1" name="form1" target="alert" method="post" enctype="multipart/form-data" style=" height:90%; overflow:auto;">
	<table id="detailId" align="center" >
	<!-- <tr><td><input id="1111" onchange="inputchanged(this)" oldvalue="111"></input></td></tr> -->
	</table>
	<div  style="height:28px;bottom: 0px; position:fixed;      left: 50%;"  >
		<input type="button" class="input2" id="save"  onclick="saveDetailtable()" value="保存"/>
	</div>
	
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