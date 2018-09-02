function  inputchanged(obj){
	var oldvalue = $(obj).val();
	var newvalue = $(obj).attr("oldvalue");
	if(oldvalue==newvalue){
		$(obj).attr("changed",0);
		$(obj).css("color","black");
	}else{
		$(obj).attr("changed",1);
		$(obj).css("color","red");
	}
	
}
/*
 * zjf  20180818 
 */
function updateDetailTable( _$data){
	var _$records = 0;
	$.ajax({
		type: "POST",
		url: url+"/GridDataZJF_readData.action",
		data: "TYPE=4"+"&ID="+_$data,
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.data.list;
			updateDetailTable_back(result);
			return true;
		}
	});
	return _$records;
}


function updateDetailTable_back(result){
	var arrayLen = result.length;
	var props = "" ;
	for (var i = 0; i < arrayLen; i++){
        props +="<tr><td>"+result[i].colname+"</td>" 
        +" <td><input onchange='inputchanged(this)' value="+result[i].datavalue
        +" oldvalue="+result[i].datavalue+" itemid="+result[i].itemid
        +" colname="+result[i].colname+"></input></td></tr>"; 
	}
	
	$("#detailId").html(props);
}

function saveClientInfo( tableDatas){
	var _$records = 0;
	$.ajax({
		type: "POST",
		url: url+"/DataManageZJF_saveSerDetail.action",
		data: "TYPE=4"+"&params="+JSON.stringify(tableDatas),
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1){
				alert("保存成功！");
			}
			return true;
		}
	});
	return _$records;
}

function saveClientInfo_back(){
	
}