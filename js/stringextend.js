/**
 * 扩展String
 */

/**
 * 替换所有String
 */
String.prototype.replaceAll  = function(s1,s2){    
	return this.replace(new RegExp(s1,"gm"),s2);    
}

String.prototype.endsWith=function(str){
	if(str==null||str==""||this.length==0||str.length>this.length)
		return false;
	if(this.substring(this.length-str.length)==str)
		return true;
	else
		return false;
	return true;
}

String.prototype.startsWith=function(str){
	if(str==null||str==""||this.length==0||str.length>this.length)
		return false;
	if(this.substr(0,str.length)==str) return true;
	else return false;
	return true;
}