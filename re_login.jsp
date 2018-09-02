<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
String url = request.getContextPath();
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=url %>/css/login_css.css"/>
<script language="javascript" type="text/javascript" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=url %>/js/login.js"></script>
<title>用户重新登录</title>
<script type="text/javascript">
$(function(){
	var cval=getCookie("shxy.com");
	if(cval==null){
		cval = "";
	}
	$("#username").val(cval);
});
function closePage(){
	var obj=window.dialogArguments;
 	if(obj.args){
 		obj.exec();
	}else{
		window.dialogArguments.window.location.href = window.dialogArguments.window.location;
	}
    window.close();
}
</script>
</head>
<body>
<div id="loginBox">
	<div style=" text-align:center"><span class="red">用户登录系统</span></div>
    <div class="formRow">
      <div class="formImage"><img src="images/user_image.jpg" alt="User"></div>
      <div class="formField">
        <input name="username" id="username" value="" alt="username" type="text" disabled="disabled"/>
      </div>
    </div>
    <div class="formRow">
      <div class="formImage"><img src="images/pass_image.jpg" alt="Pass"></div>
      <div class="formField">
        <input name="password" id="password" alt="password" type="password"/>
      </div>
    </div>
    <div class="formRow">
      <input id="reLoginButton" class="loginButton" src="images/login_button.jpg" alt="Login" type="image">
    </div>
</div>
</body>
</html>