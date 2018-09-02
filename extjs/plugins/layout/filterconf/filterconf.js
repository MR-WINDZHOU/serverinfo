Ext.require(['*']);

Ext.onReady(function() {
    
    Ext.define('fields', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'table', type: 'string'},
            {name: 'table_ZH', type: 'string'}
         ]
    });
    
    // 业务表字段加载工厂
    tablesStore = Ext.create('Ext.data.ArrayStore', {
        model: 'fields',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_readData.action?type=12&config='+configRecord,
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    tablesStore.load();
    
    // declare the source Grid
    tablesGrid = Ext.create('Ext.grid.Panel', {
        store: tablesStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "业务表名", flex: 1, sortable: true, dataIndex: 'table_ZH'}
	    ],
        stripeRows: true,
        height: '100%',
        border: false,
        autoScroll: true,
        viewConfig:{
	        listeners: {
				itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
					filterInfo(rec.get("table"));
				}
			}
		}
    });
    
    Ext.create('Ext.Viewport', {
        layout: {
            type: 'border',
            padding: 0
        },
        defaults: {
            split: true
        },
        items: [{
            region: 'west',
            collapsible: true,
            title: '业务表',
            split: true,
            width: 210,
            floatable: true,
            items: tablesGrid,
	        listeners:{
	        	resize: function(obj, width, height){
	        		tablesGrid.setWidth(width-2);
	        		tablesGrid.setHeight(height-28);
	        	}
	       	}
        },{
        	region: 'center',
	        title: '过滤条件',
	        html:'<form id="form1" name="form1" action="DataManage_saveSubmitInfo.action" target="alert_tmp" method="post"><textarea id="TAB_051_002" name="TAB_051_002" '+(edit?"disabled=true":"")+'></textarea><input type="hidden" name="table" value="TAB_4012"/></form>',
	        dockedItems: [{
	            xtype: 'toolbar',
	            items: [{
	                text:'保存',
	                tooltip:'保存',
	                iconCls:'save',
	                disabled: edit,
	                handler: function(){
	                	$("#form1").submit();
	                }
	            }]
			}]
        }]
    });
});

/**
 * 保存报表过滤信息
 */
function filterInfo(table){
	$.ajax({
		type: "POST",
		url: url+"/DataManage_operate.action",
		async: false,
		data: "type=8&tab_045_id="+configRecord+"&tab_051_001="+table,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1){
				if(data.filter&&data.filter!=""){
					$("#TAB_051_002").text(data.filter);
					$("#form1").append("<input id='id' name='id' value='"+data.id+"'>");
				}else{
					$("#TAB_051_002").text("");
					$("#form1").append("<input name='tab_045_id' value='"+configRecord+"'>");
					$("#form1").append("<input name='tab_051_001' value='"+table+"'>");
					$("#id").remove();
				}
			}else if(result==2){
				alert('信息保存失败!');
			}else if(result==-1){
				alert('数据库未开启，请先开启数据库后再操作！');
			}else{
				alert('操作失败!');
			}
		}
	});
}