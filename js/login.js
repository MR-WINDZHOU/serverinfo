$(document).ready(function(){
	$("#reLoginButton").click(function(){
		$.ajax({
		   type: "POST",
		   url: "userlogin_verify.action",
		   data: "username=" +$("#username").val() +"&password=" +$("#password").val(),
		   complete:function(request){
			    var result=parseInt(request.responseText);
			    if(result==1){
			    	closePage();
        		 	return;
        		 }else if(result==2){
        		 	alert('用户名、密码错误，请重新输入!');
        		 	return;
        		 }
		   }
		}); 
	});
});

/**
 * 回车键事件
 */
document.onkeydown = function(evt){
	var evt = window.event?window.event:evt;
	if(evt.keyCode==13){
		if($(".loginsure").length>0){
			$(".loginsure").click();
		}
		if($("#reLoginButton").length>0){
			$("#reLoginButton").click();
		}
	}
}


/**
 * 写cookies函数
 */
function SetCookie(name,value)//两个参数，一个是cookie的名子，一个是值
{
	var Days = 30; //此 cookie 将被保存 30 天
	var exp  = new Date();    //new Date("December 31, 9998");
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	document.cookie = name+"="+escape(value)+";expires="+exp.toGMTString();
}
function getCookie(name)//取cookies函数        
{
	var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
	if(arr != null) return unescape(arr[2]); return null;
}
function delCookie(name)//删除cookie
{
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval=getCookie(name);
	if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}
$(function(){
	var cval=getCookie("shxy.com");
	if(cval==null){
		cval = "";
		$("#USERNAME").focus();
	}else{
		$("#USERPWD").focus();
	}
	$("#USERNAME").val(cval);
	$(".loginsure").click(function(){
		var userName = $("#USERNAME").val();
		var password = $("#USERPWD").val();
		if(userName.length==0){
			alert("用户名不能为空！");
			$("#USERNAME").focus();
			$("#USERNAME").select();
			return false;
		}
		if(password.length==0){
			alert("密码不能为空！");
			$("#USERPWD").focus();
			$("#USERPWD").select();
			return false;
		}
	
		$.ajax({
		   type: "POST",
		   url: "userlogin_verify.action",
		   data: "username=" +userName+"&password="+password,
		   complete:function(request){
			    var result=parseInt(request.responseText);
			    if(result==1){
					delCookie("shxy.com");
					if($("#savename").attr("checked")){
						SetCookie("shxy.com", userName);
					}
        		 	document.location = url+"/DataManage_read.action?url=/extjs/plugins/portal/portal.jsp";
        		 	return;
        		 }else if(result==2){
        		 	alert('用户名、密码错误，请重新输入!');
        		 	return;
        		 }
		   }
		});
	});
	$(".loginreset").click(function(){
		var cval=getCookie("shxy.com");
		if(cval==null){
			cval = "";
		}
		$("#USERNAME").val(cval);
		$("#USERPWD").val("");
		$("#USERPWD").focus();
	});
});