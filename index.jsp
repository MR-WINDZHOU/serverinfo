<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%
String url = request.getContextPath();
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>首页 登录页面</title>
<link rel="stylesheet" type="text/css" charset="utf-8" href="<%=url %>/css/page.css"/>
<script language="javascript" type="text/javascript" charset="utf-8" src="<%=url %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" charset="utf-8" src="<%=url %>/js/login.js"></script>
</head>
<body>
<div class="middle">
	<div class="xmiddle">
		<div class="left"></div>
		<div class="middle_login">
			<div class="head_middle"></div>
			<div class="middle_middle">
				<div class="login_img"></div>
				<div class="login_middle">
					<div class="main_head"></div>
					<div class="main_content">
						<table class="login_table">
							<tr><td>请输入用户名：</td></tr>
							<tr><td><input type="text" class="namepwd" id="USERNAME" name="USERNAME"></td></tr>
							<tr><td>密&nbsp;&nbsp;码：</td></tr>
							<tr><td><input type="password" class="namepwd" id="USERPWD" name="USERPWD"/></td></tr>
							<tr><td><input type="checkbox" id="savename" checked="checked">记住用户名</td></tr>
							<tr><td style="padding:5px;"><input type="button" class="loginsure"/><input type="button" class="loginreset"/></td></tr>
						</table>
					</div>
				</div>
			</div>
			<div class="feed_middle"></div>
		</div>
		<div class="right"></div>
	</div>
</div>
<div class="login_head"></div>
<div class="login_feed"></div>
</body>
</html>