Ext.require(['*']);

Ext.onReady(function() {

	Ext.define('configs', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'table', type: 'string', edit: true},
            {name: 'table_ZH', type: 'string', edit: true},
            {name: 'field', type: 'string', edit: true},
            {name: 'field_ZH', type: 'string', edit: true},
            {name: 'show', type: 'string', edit: true},
            {name: 'show_ZH', type: 'string', edit: true},
            {name: 'label_ID', type: 'string'},
            {name: 'label_Name', type: 'string'},
            {name: 'title', type: 'string', edit: true},
            {name: 'js', type: 'string', edit: true},
            {name: 'date_name', type: 'string', edit: true},
            {name: 'date_type', type: 'string', edit: true}
         ]
    });
    
	var configsStore = Ext.create('Ext.data.ArrayStore', {
        model: 'configs'
    });
    
	var iframe = document.createElement("iframe");
	iframe.id = "pageConfig";
	iframe.src = url + pageUrl;
	iframe.style.display = "none";
	
	if (iframe.attachEvent){
	    iframe.attachEvent("onload", function(){
	        for(var i=0;;i++){
		    	var _$obj = getIframeObj("pageConfig").document.getElementById("a"+i);
		    	if(_$obj){
		    		alert(_$obj.previousSibling.innerHTML);
		    	}else{
		    		break;
		    	}
		    }
	    });
	} else {
	    iframe.onload = function(){
	        for(var i=0;;i++){
		    	var _$obj = $(getIframeObj("pageConfig").document.getElementById("a"+i));
		    	if(_$obj.length>0){
		    		configsStore.add({"label_ID": "a"+i, "label_Name":_$obj.prev().html()});
		    	}else{
		    		break;
		    	}
		    }
	    };
	}
	document.body.appendChild(iframe);
	
	Ext.define('tree', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {name: 'id',  type: 'string'},
	        {name: 'text',  type: 'string'},
	        {name: 'leaf',  type: 'boolean'},
	        {name: 'business',  type: 'string'}
	    ]
	});
	
	
	Ext.define('fields', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'table', type: 'string'},
            {name: 'table_ZH', type: 'string'},
            {name: 'field', type: 'string'},
            {name: 'field_ZH', type: 'string'},
            {name: 'show', type: 'string'},
            {name: 'title', type: 'string'},
            {name: 'show_ZH', type: 'string'}
         ]
    });
    
    var store = Ext.create('Ext.data.TreeStore', {
    	model: 'tree',
        proxy: {
            type: 'ajax',
            url: 'TreeData_tree.action?type=2'
        },
        root: {
            text: '业务、环节树',
            id: 'business',
            expanded: true
        }
    });
    
    var fieldsStore = Ext.create('Ext.data.ArrayStore', {
        model: 'fields',
        groupField: 'table_ZH',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_readData.action?type=6',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    
    var tree1 = Ext.create('Ext.tree.Panel', {
        store: store,
        useArrows: true,
       	border:0,
        viewConfig: {
            listeners: {
				itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
					if(rec.get("leaf")){
						var _$proxy = fieldsStore.getProxy();
					    _$proxy.extraParams["business"] = rec.get("business");
					    fieldsStore.load();
					}
					view.expand(rec);//单击展开子节点
				}
            }
        }
    });
    
    // declare the source Grid
    var fieldsGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdropowj',
                dragGroup: 'firstGridDDGroup'
            }
        },
        store: fieldsStore,
        columns: [
	        {text: "栏目名称", flex: 1, sortable: true, dataIndex: 'field_ZH'},
	        {text: "英文栏目名", flex: 1, sortable: true, dataIndex: 'field'}
	    ],
	    features: [Ext.create('Ext.grid.feature.Grouping',{
	        groupHeaderTpl: '{name} (共 {rows.length} 个字段)',
	        menushow: false
	    })],
        stripeRows: true,
        border: false,
        autoScroll: true
    });
    
    Ext.define('datetype', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {name: 'date_type',  type: 'int'},
	        {name: 'date_name',  type: 'string'}
	    ]
	});
	
    var datetypesStore = Ext.create('Ext.data.ArrayStore', {
        model: 'datetype',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_sqliteData.action?type=7',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    datetypesStore.load();
    
    var datetypesGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdropowj',
                dragGroup: 'firstGridDDGroup'
            }
        },
        store: datetypesStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "日期类型", flex: 1, sortable: true, dataIndex: 'date_name'}
	    ],
        stripeRows: true,
        border: false,
        autoScroll: true
    });
    
    Ext.define('function', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {name: 'name',  type: 'string'},
	        {name: 'js',  type: 'string'}
	    ]
	});
    var functionsStore = Ext.create('Ext.data.ArrayStore', {
        model: 'function',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_sqliteData.action?type=8',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    functionsStore.load();
    
    var functionsGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdropowj',
                dragGroup: 'firstGridDDGroup'
            }
        },
        store: functionsStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "方法名", flex: 1, sortable: true, dataIndex: 'name'},
	        {text: "方法代码", flex: 1, sortable: true, dataIndex: 'js'}
	    ],
        stripeRows: true,
        border: false,
        autoScroll: true
    });
    
    var configsGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdropowj',
                dropGroup: 'firstGridDDGroup'
            }
        },
        store: configsStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "标签号", width: 45, sortable: false, dataIndex: 'label_ID'},
	        {text: "标签名", flex: 1, sortable: false, dataIndex: 'label_Name'},
	        {text: "业务表名", flex: 1, sortable: false, dataIndex: 'table_ZH'},
	        {text: "字段英文名", flex: 1, sortable: false, dataIndex: 'field'},
	        {text: "字段中文名", flex: 1, sortable: false, dataIndex: 'field_ZH'},
	        {text: "标签标题", flex: 1, sortable: false, dataIndex: 'title'},
	        {text: "字段类型", flex: 1, sortable: false, dataIndex: 'show_ZH'},
	        {text: "日期显示类型", flex: 1, sortable: false, dataIndex: 'date_name'},
	        {text: "触发方法", flex: 1, sortable: false, dataIndex: 'js'}
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
            title: '业务、环节',
            split: true,
            width: 210,
            floatable: true,
            items:[tree1],
            listeners:{
	        	resize: function(obj, width, height){
	        		tree1.setWidth(width-2);
	        		tree1.setHeight(height-28);
	        	}
	       	}
        },{
            region: 'center',
            border: 0,
            layout: {
                type: 'border'
            },
            items:[{
	            region: 'west',
	            collapsible: true,
	            title: '业务表字段',
	            split: true,
	            width: 210,
	            floatable: true,
	            items:[fieldsGrid],
	            listeners:{
		        	resize: function(obj, width, height){
		        		fieldsGrid.setWidth(width-2);
		        		fieldsGrid.setHeight(height-28);
		        	}
		       	}
	        },{
                region: 'center',
                collapseMode: 'mini',
	            title: '字段编辑区域',
                items:configsGrid,
                tbar: [{
		            text: '编辑多记录表',
		            iconCls: 'edit_multi_table',
		            handler : function() {
		            }
		        },'-',{
		            text: '保存',
		            iconCls: 'save',
		            handler: function() {
		            }
		        },'-',{
		            text: '导出XML',
		            iconCls: 'export_xml',
		            handler: function() {
		            }
		        }]
            },{
	            region: 'east',
	            title: '转换类型及转换格式',
	            split:true,
	            width: 200,
	            minSize: 175,
	            maxSize: 400,
	            collapsible: true,
	            layout:'accordion',
	            layoutConfig:{
	                animate:true
	            },
	            items: [{
	                title:'日期显示类型',
	                autoScroll:true,
	                border:false,
	                iconCls:'calendar_icon',
	                items: datetypesGrid
	            },{
	                title:'字段调用方法',
	                border:false,
	                autoScroll:true,
	                iconCls:'script_icon',
	                items: functionsGrid
	            }],
	            listeners: {
	            	resize: function(obj, width, height){
	            		datetypesGrid.setWidth(width-2);
	            		functionsGrid.setWidth(width-2);
	            		datetypesGrid.setHeight(height-80);
	            		functionsGrid.setHeight(height-80);
	            	}
	            }
	        }]
        }]
    });
});
