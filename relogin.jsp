<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String url = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<base target="_self"/>
<body>
<script type="text/javascript">
	alert("系统长时间未操作，请重新登录！");
	var _$iTop=(window.screen.height-270)/2;
    var _$iLeft=(window.screen.width-390)/2;
	window.showModalDialog("<%=url %>/re_login.jsp",window,'dialogWidth:390px;dialogHeight:270px;dialogLeft:'+_$iLeft+';dialogTop:'+_$iTop+';center:yes;help:no;resizable:no;status:no;titlebar=no,toolbar=no,location=no,Directories=no,status=no,menubar=no,scrollbars=no,resizable=no');
</script>
</body>
</html>