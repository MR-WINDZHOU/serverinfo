/**
 * 异步保存信息，保存成功后弹出alert
 * 参数：
 *   _$url  保存的地址
 *   _$data 保存的数据
 * 进行操作：有提示 不进行任何操作 不返回任何内容
 */
function saveSubmit(_$url, _$data){
	$.ajax({
		type: "POST",
		url: _$url,
		data: _$data,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1){
				alert('信息更新成功!');
			}else if(result==2){
				alert('信息更新失败!');
			}else if(result==-1){
				alert('数据库未开启，请先开启数据库后再操作！');
			}else{
				alert('操作失败!');
			}
			return;
		}
	});
}

/**
 * 同步保存信息，保存成功后弹出alert框
 * 参数：
 *   _$url  保存的地址
 *   _$data 保存的数据
 * 进行操作：有提示 不进行任何操作 返回是否保存成功信息
 */
var insertRecord = 0;
function saveSubmitAsync(_$url, _$data){
	var saveResult = false;
	$.ajax({
		type: "POST",
		url: _$url,
		async: false,
		data: _$data,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			insertRecord = 0;
			var result=data.result;
			if(result==1){
				alert('信息保存成功!');
				if(data.record){
					insertRecord = data.record;
				}
				saveResult = true;
			}else if(result==2){
				alert('信息保存失败!');
			}else if(result==-1){
				alert('数据库未开启，请先开启数据库后再操作！');
			}else{
				alert('操作失败!');
			}
			return saveResult;
		}
	});
	return saveResult;
}

/**
 * 同步保存信息(不弹出alert框)
 * 参数：
 *   _$url  保存的地址
 *   _$data 保存的数据
 * 进行操作：没有提示 不进行任何操作 不返回任何内容
 */
function saveSubmitAsyncNoAlert(_$url, _$data){
	var saveResult = false;
	$.ajax({
		type: "POST",
		url: _$url,
		async: false,
		data: _$data,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			insertRecord = 0;
			var result=data.result;
			if(result==1){
				saveResult = true;
				if(data.record){
					insertRecord = data.record;
				}
			}else if(result==2){
				alert('信息更新失败!');
			}else if(result==-1){
				alert('数据库未开启，请先开启数据库后再操作！');
			}else{
				alert('操作失败!');
			}
			return saveResult;
		}
	});
	return saveResult;
}

/**
 * 异步删除表内容，删除后调用closeDialog方法
 * 参数：
 *   _$table   要删除记录的表名
 *   _$records 要删除记录的ID
 * 进行操作：有提示 不返回任何内容 如果页面有closeDialog方法 则调用
 */
function deleteAfterClose(_$table, _$records){
	$.ajax({
		type: "POST",
		url: "DataManage_deleteAjax.action",
		data: "TABLE="+_$table+"&RECORDS="+_$records,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1){
				alert('记录删除成功!');
				if(typeof(closeDialog)=='function'){
					closeDialog();
				}
			}else if(result==2){
				alert('记录删除失败!');
			}else if(result==-1){
				alert('数据库未开启，请先开启数据库后再操作！');
			}else{
				alert('操作失败!');
			}
			return;
		}
	});
}

/**
 * 同步删除表记录，不调用closeDialog方法
 * 参数：
 *   _$table   要删除记录的表名
 *   _$records 要删除记录的ID
 * 进行操作：有提示 不返回任何内容
 */
function deleteNoClose(_$table, _$records){
	$.ajax({
		type: "POST",
		url: "DataManage_deleteAjax.action",
		data: "TABLE="+_$table+"&RECORDS="+_$records,
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1){
				alert('记录删除成功!');
			}else if(result==2){
				alert('记录删除失败!');
			}else if(result==-1){
				alert('数据库未开启，请先开启数据库后再操作！');
			}else{
				alert('操作失败!');
			}
			return;
		}
	});
}

/**
 * 通过url同步删除表记录，弹出提示框
 * 参数：
 *   _$table   要删除记录的表名
 *   _$records 要删除记录的ID
 * 进行操作：有提示 不返回任何内容
 */
function deleteNoCloseOfURL(_$url, _$data){
	var deleteResult = false;
	$.ajax({
		type: "POST",
		url: _$url,
		data: _$data,
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1){
				deleteResult = true;
				alert('记录删除成功!');
			}else if(result==2){
				alert('记录删除失败!');
			}else if(result==-1){
				alert('数据库未开启，请先开启数据库后再操作！');
			}else{
				alert('操作失败!');
			}
			return;
		}
	});
	return deleteResult;
}

/**
 * 同步判断表记录是否存在，不弹出alert框
 * 参数：
 *   _$table  要比较记录的表名
 *   _$data   要比较记录的数据
 * 进行操作：无提示 返回记录个数
 */
function existNoAlert(_$table, _$data){
	var _$records = 0;
	$.ajax({
		type: "POST",
		url: url+"/DataManage_existAjax.action",
		data: "TABLE="+_$table+"&"+_$data,
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1) _$records = data.records; else if(result==2) alert('表不存在!'); else if(result==0) alert('参数错误!'); else if(result==3) alert('数据库异常 异常信息：'+data.exception+'!'); else alert('操作失败!');
			return true;
		}
	});
	return _$records;
}

/**
 * 同步判断表记录是否存在，不弹出alert框
 * 参数：
 *   _$table  要比较记录的表名
 *   _$data   要比较记录的数据
 * 进行操作：无提示 返回记录个数
 */
function existSqliteNoAlert(_$table, _$data){
	var _$records = 0;
	$.ajax({
		type: "POST",
		url: url+"/DataManage_existSqliteAjax.action",
		data: "TABLE="+_$table+"&"+_$data,
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1) _$records = data.records; else if(result==2) alert('表不存在!'); else if(result==0) alert('参数错误!'); else if(result==3) alert('数据库异常 异常信息：'+data.exception+'!'); else alert('操作失败!');
			return true;
		}
	});
	return _$records;
}

