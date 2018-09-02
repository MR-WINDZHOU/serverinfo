function getIframeObj(iframe)
{
	var obj;
	if (document.all){//IE浏览器
        obj = document.frames[iframe];
	}else{//Firefox浏览器    
	    obj = document.getElementById(iframe).contentWindow;
	}
	return obj;
}