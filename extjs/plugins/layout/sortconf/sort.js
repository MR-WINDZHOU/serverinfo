Ext.require(['*']);

Ext.onReady(function() {
    
    Ext.define('fields', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'before', type: 'int'},
            {name: 'table', type: 'string'},
            {name: 'table_ZH', type: 'string'},
            {name: 'field', type: 'string'},
            {name: 'field_ZH', type: 'string'},
            {name: 'sort', type: 'boolean'},
            {name: 'target', type: 'boolean'}
         ]
    });
    
    // 业务表字段加载工厂
    fieldStore = Ext.create('Ext.data.ArrayStore', {
        model: 'fields',
        groupField: 'table_ZH',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_readData.action?type=10&config='+configRecord,
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    fieldStore.load();
    
    // 排序已配置字段加载工厂
    sortsConfigStore = Ext.create('Ext.data.ArrayStore', {
        model: 'fields',
        groupField: 'table_ZH',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_readData.action?type=11&config='+configRecord,
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    sortsConfigStore.load();

    // declare the source Grid
    fieldsGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup'
            }
        },
        features: [Ext.create('Ext.grid.feature.Grouping',{
	        groupHeaderTpl: '{name} (共 {rows.length} 个字段)'
	    })],
        store: fieldStore,
        columns: [
	        {text: "业务表名", flex: 1, sortable: true, dataIndex: 'table_ZH'},
	        {text: "栏目名称", flex: 1, sortable: true, dataIndex: 'field_ZH'}
	    ],
        stripeRows: true,
        height: '100%',
        border: false,
        autoScroll: true
    });
    
    // declare the source Grid
    sortsConfigGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                beforedrop: function(node, data, dropRec, dropPosition) {
                	if(edit){
                		alert("报表已启用，不可编辑！");
                		return false;
                	}
                	var _$placeOrder = dropRec?dropRec.get("id"):"0";
                	if(dropPosition=="before"){
                		_$placeOrder = dropRec?dropRec.get("before"):"0";
                	}
                	if(data.records[0].get("target")){//调整顺序
                		if(!saveSubmitAsyncNoAlert(url+"/DataManage_operate.action", "type=6&before="+data.records[0].get("id")+"&after="+_$placeOrder)){
                			return false;
                		}
                	}else{//插入
                		var _$params = "type=5&TAB_045_ID="+configRecord+"&TAB_050_001="+data.records[0].get("table")+"&TAB_050_002="+data.records[0].get("field")+"&after="+_$placeOrder;
                		if(!saveSubmitAsyncNoAlert(url+"/DataManage_operate.action", _$params)){
                			return false;
                		}
                	}
               		sortsConfigStore.load();
                	return true;
                },
		        itemcontextmenu: function(view, rec, node, index, e) {
		        	View = view;
					Rec = rec;
					e.stopEvent();
                    if(this.menuInfo){this.menuInfo.close();}
	        		this.menuInfo = new Ext.menu.Menu({floating: true, items:[
	        		{
	        			text: '删除',
	        			icon : url+'/extjs/plugins/shared/icons/menu/delete.png',
	        			disabled: edit,
						handler : function() {
							if(!confirm("你确定删除该配置字段？")){
								return false;
							}
		                	if(saveSubmitAsyncNoAlert(url+"/DataManage_deleteAjax.action", "table=TAB_4011&RECORDS="+rec.get("id"))){
		                		sortsConfigStore.load();
		                	}
						}
	        		}] });
		        	this.menuInfo.showAt(e.getXY());
                    return false;
                }
            }
        },
        features: [Ext.create('Ext.grid.feature.Grouping',{
	        groupHeaderTpl: '{name} (已使用 {rows.length} 个字段)'
	    })],
        store: sortsConfigStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "业务表名", flex:1, sortable: true, dataIndex: 'table_ZH'},
	        {text: "栏目名称", flex:1, sortable: true, dataIndex: 'field_ZH'},
	        {text: "排序方式", flex:1, sortable: true, dataIndex: 'sort',renderer: function(val, me, rec){return '<input type=radio name="'+rec.get("id")+'_checkbox" value="1" '+(val?"checked=true":"")+' onclick="changeSort(this)" '+(edit?"disabled=true":"")+'>升序&nbsp;&nbsp;<input type=radio name="'+rec.get("id")+'_checkbox" value="0" '+(val?"":"checked=true")+' onclick="changeSort(this)" '+(edit?"disabled=true":"")+'>降序';}}
	    ],
        stripeRows: true,
        height: '100%',
        border: false,
        autoScroll: true
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
            title: '业务表及包含的字段',
            split: true,
            width: 210,
            floatable: true,
            items: fieldsGrid,
	        listeners:{
	        	resize: function(obj, width, height){
	        		fieldsGrid.setWidth(width-2);
	        		fieldsGrid.setHeight(height-28);
	        	}
	       	}
        },{
        	region: 'center',
	        title: '报表所需排序条件',
	        items: sortsConfigGrid,
	        listeners:{
	        	resize: function(obj, width, height){
	        		sortsConfigGrid.setWidth(width-2);
	        		sortsConfigGrid.setHeight(height-28);
	        	}
	       	}
        }]
    });
});

/**
 * 更新排序方式(升序还是降序)
 */
function changeSort(th){
	if(saveSubmitAsyncNoAlert(url+"/DataManage_saveSubmitInfoAjax.action", "table=TAB_4011&id="+$(th).attr("name").split("_")[0]+"&TAB_050_003="+$(th).val())){
		return true;
	}
	$("input[type=checkbox][name="+$(th).attr("name")+"]").attr("checked", !$("input[type=checkbox][name="+$(th).attr("name")+"]").attr("checked"));
	return false;
}