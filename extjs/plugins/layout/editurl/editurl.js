Ext.Loader.setConfig({enabled: true}); 

Ext.Loader.setPath('Ext.ux', url+'/extjs/plugins/ux/');

Ext.require(['*','Ext.ux.form.SearchField','Ext.selection.CheckboxModel']);

Ext.onReady(function() {
	var _$projects = "",
		_$authors = "",
		_$starttime = new Date().pattern("yyyy-MM-dd"),
		_$endtime = new Date().pattern("yyyy-MM-dd"),
		_$selectUrls = "",
		_$updateUrls = "";
	
	Ext.define('Ext.grid.RowUpdater', {
	    extend: 'Ext.grid.column.Column',
	    alias: 'widget.rowupdater',
	    text: "&#160",
	    width: 23,
	    sortable: false,
	    align: 'center',
	    constructor : function(config){
	        this.callParent(arguments);
	        if (this.rowspan) this.renderer = Ext.Function.bind(this.renderer, this);
	    },
	    fixed: true,
	    hideable: false,
	    menuDisabled: true,
	    dataIndex: '',
	    cls: Ext.baseCSSPrefix + 'row-numberer',
	    rowspan: undefined,
	    renderer: function(value, metaData, record, rowIdx, colIdx, store) {
	        if (this.rowspan) metaData.cellAttr = 'rowspan="'+this.rowspan+'"';
	        metaData.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
	        return record.get("update")?"<img src='"+url+"/extjs/plugins/shared/icons/fam/accept.png' title='文件已升级'/>":"<img src='"+url+"/extjs/plugins/shared/icons/fam/messagebox_warning.png' title='文件未升级'/>";
	    }
	});
	
    Ext.define('projecturl', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'url', type: 'string'},
            {name: 'author', type: 'string'},
            {name: 'time', type: 'date', dateFormat: 'Y-m-d h:i:s'},
            {name: 'project', type: 'string'},
            {name: 'operate', type: 'string'},
            {name: 'update', type: 'boolean'}
         ]
    });
    
    Ext.define('projecttype', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'name', type: 'string'}
         ]
    });
    
    Ext.define('userlist', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'show', type: 'string'}
         ]
    });
    
    // 用户数据源加载工厂
    usersStore = Ext.create('Ext.data.ArrayStore', {
        model: 'userlist',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_sqliteData.action?type=1',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    usersStore.load();
    
    // 项目类型 加载工厂
    typesStore = Ext.create('Ext.data.ArrayStore', {
        model: 'projecttype',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_sqliteData.action?type=2',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    typesStore.load();
    
    // 修改的路径 加载工厂
    urlsStore = Ext.create('Ext.data.ArrayStore', {
        model: 'projecturl',
        proxy: {
        	actionMethods: {
		        create: 'POST',
		        destroy: 'DELETE',
		        read: 'POST',
		        update: 'POST'
		    },
            type: 'ajax',
            url: url+'/GridData_sqliteData.action?type=3',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    _$proxy = urlsStore.getProxy(),
    _$proxy.extraParams["STARTTIME"] = _$starttime;
    _$proxy.extraParams["ENDTIME"] = _$endtime;
    urlsStore.load();
    
    urlsGrid = Ext.create('Ext.grid.Panel', {
        store: urlsStore,
        selModel: Ext.create('Ext.selection.CheckboxModel', {
	        listeners: {
	            selectionchange: function(sm, selections) {//选中事件(选中和取消选中)
	                Ext.getCmp("exporturls").setDisabled(selections.length<=0);
	                Ext.getCmp("delurl").setDisabled(selections.length<=0);
	                //选中的url主键ID
	                _$selectUrls = $.map(selections, function(val, i){return val.get("id");}).join(",");
	                //选中的记录未上报的主键IDs
	                _$updateUrls = $.map(selections, function(val, i){return val.get("update")?null:val.get("id");}).join(",");//过滤已上报的文件
	                Ext.getCmp("updateurls").setDisabled(_$updateUrls.length<=0);
	            }
        	}
        }),
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
        	Ext.create('Ext.grid.RowUpdater'),
	        {text: "项目分类", width:100, sortable: false, dataIndex: 'project'},
	        {text: "功能/业务名称", width:100, sortable: false, dataIndex: 'operate'},
	        {text: "修改人", width:100, sortable: false, dataIndex: 'author'},
	        {text: "修改日期", width:120, sortable: false, dataIndex: 'time',renderer: Ext.util.Format.dateRenderer('Y-m-d h:i:s')},
	        {text: "项目修改路径(URL)", flex: 1, sortable: false, dataIndex: 'url'}
	    ],
        stripeRows: true,
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
        	region: 'center',
	        title: '项目修改路径',
	        items: urlsGrid,
	        listeners:{
	        	resize: function(obj, width, height){
	        		urlsGrid.setWidth(width-2);
	        		urlsGrid.setHeight(height-110);
	        	}
	       	},
	       	tbar: [{
	            xtype: 'buttongroup',
	            title: '操作',
	            columns: 2,
	            defaults: {
	                scale: 'small'
	            },
	            items: [{
	                text	: '添加路径',
	                tooltip	: '添加路径',
	                height	: 28,
	                iconCls	: 'addurl',
	                handler	: function(){
	                	parent.addUrl();
	                }
	            },{
	            	id		: 'delurl',
	                text	: '删除路径',
	                tooltip	: '删除选中路径',
	                iconCls	: 'delurl',
	                hidden	: !manage,
	                disabled: true,
	                height	: 28,
	                handler	: function(){
	                	if(deleteNoCloseOfURL(url+"/DataManage_sqliteDeleteAjax.action", "table=SYSTAB205&DELETEFIELD1=ID&DELETERECORDS1="+_$selectUrls)){
							urlsStore.load();
						}
	                }
	            },{
	            	id		: 'exporturls',
	                text	: '导出路径',
	                tooltip	: '导出选中路径到文本',
	                iconCls	: 'export_document',
	                disabled: true,
	                height	: 28,
	                handler	: function(){
	                	getIframeObj("alert_tmp").location = url+"/ExportFile_sqliteExport.action?type=1&records="+_$selectUrls;
	                }
	            },{
	            	id		: 'updateurls',
	                text	: '文件已升级',
	                tooltip	: '点击可将文件置为已升级状态',
	                iconCls	: 'update',
	                height	: 28,
	                hidden	: !manage,
	                disabled: true,
	                handler: function(){
	                	if(saveSubmitAsyncNoAlert(url+"/DataManage_saveSqliteAjax.action", "table=systab205&S005=1&ID="+_$updateUrls)){
	                		urlsStore.load();
	                	}
	                }
	            }]
	        },{
	            xtype: 'buttongroup',
	            title: '简单查询',
	            columns: 2,
	            defaults: {
	                scale: 'small'
	            },
	            items: [{
	            	id: 'startTime',
	                fieldLabel: '筛选时间', 
	                labelWidth: 60, 
	                width: 160, 
	                xtype: 'datefield',
	                format: 'Y-m-d',
	                value: new Date(),
	                maxValue: new Date(),
	                listeners:{
				    	change: function(field, newVal, oldVal){
				    		if(newVal.pattern("yyyy-MM-dd")!=oldVal.pattern("yyyy-MM-dd")){
				    			_$starttime1 = Ext.getCmp("startTime").getValue().pattern("yyyy-MM-dd");
				    			_$endtime1 = Ext.getCmp("endTime").getValue().pattern("yyyy-MM-dd");
				    			
						        _$proxy.extraParams["STARTTIME"] = _$starttime1;
						        _$proxy.extraParams["ENDTIME"] = _$endtime1;
				    			urlsStore.load();
						        
							    _$starttime = _$starttime1;
							    _$endtime = _$endtime1;
				    		}
				    	}
				    }
	            },{
	            	id:'endTime',
	                fieldLabel: '&nbsp;&nbsp;～',
	                labelWidth: 18, 
	                labelSeparator:"", 
	                width: 118, 
	                xtype: 'datefield', 
	                format: 'Y-m-d',
	                value: new Date(),
	                minValue: new Date(),
	                listeners:{
				    	change: function(field, newVal, oldVal){
				    		if(newVal.pattern("yyyy-MM-dd")!=oldVal.pattern("yyyy-MM-dd")){
				    			_$starttime1 = Ext.getCmp("startTime").getValue().pattern("yyyy-MM-dd");
				    			_$endtime1 = Ext.getCmp("endTime").getValue().pattern("yyyy-MM-dd");
				    			
						        _$proxy.extraParams["STARTTIME"] = _$starttime1;
						        _$proxy.extraParams["ENDTIME"] = _$endtime1;
				    			urlsStore.load();
						        
							    _$starttime = _$starttime1;
							    _$endtime = _$endtime1;
				    		}
				    	}
				    }
	            },{
	            	id: 'seach',
	                width: 279,
	                colspan: 2,
	                fieldLabel: '搜索条件', 
	                labelWidth: 60, 
	                xtype: 'searchfield', 
	                store: urlsStore,
	                scope:this
	            }]
	    	},{
	            xtype: 'buttongroup',
	            title: '类别查询',
	            columns: 2,
	            defaults: {
	                scale: 'small'
	            },
	            items: [{
	            	id:'project',
	                fieldLabel: '项目名',
	                labelWidth: 45,
	                width: 145,
	                xtype: 'combobox',
	                queryMode: 'local',
				    store: typesStore,
	                multiSelect: true,
				    displayField: 'name',
				    emptyText:'所有项目',
				    valueField: 'id',
	                listeners:{
				    	change: function(field, newVal, oldVal){
				    		if(_$projects!=newVal||_$authors!=Ext.getCmp("author").getValue()){
				    			Ext.getCmp("serviceseach").setDisabled(false);
				    		}else{
								Ext.getCmp("serviceseach").setDisabled(true);
				    		}
				    	}
				    }
	            },{
	            	id: 'serviceseach',
	                text:'搜索',
	                rowspan: 2,
	                tooltip:'搜索',
	                iconCls:'zoom',
	                disabled: true,
	                handler: function(){
	                	Ext.getCmp("serviceseach").setDisabled(true);
	                	var _$authors1 = Ext.getCmp("author").getValue();
		    			var _$projects1 = Ext.getCmp("project").getValue();
		    			_$authors1 = (_$authors1==undefined?"":_$authors1);
		    			_$projects1 = (_$projects1==undefined?"":_$projects1);
		    			_$starttime1 = Ext.getCmp("startTime").getValue().pattern("yyyy-MM-dd");
		    			_$endtime1 = Ext.getCmp("endTime").getValue().pattern("yyyy-MM-dd");
		    			
				        _$proxy.extraParams["S002"] = _$authors1;
				        _$proxy.extraParams["S003"] = _$projects1;
				        _$proxy.extraParams["STARTTIME"] = _$starttime1;
				        _$proxy.extraParams["ENDTIME"] = _$endtime1;
		    			urlsStore.load();
				        
					    _$projects = _$projects;
					    _$authors = _$authors;
					    _$starttime = _$starttime1;
					    _$endtime = _$endtime1;
	                }
	            },{
	            	id: 'author',
	                fieldLabel: '修改人',
	                labelWidth: 45,
	                valueSeparator:',',
	                width: 145,
	                xtype: 'combobox',
	                multiSelect: true,
	                queryMode: 'local',
				    store: usersStore,
				    displayField: 'show',
				    emptyText:'所有人',
				    valueField: 'id',
	                listeners:{
				    	change: function(field, newVal, oldVal){
				    		if(_$authors!=newVal||_$projects!=Ext.getCmp("project").getValue()){
				    			Ext.getCmp("serviceseach").setDisabled(false);
				    		}else{
								Ext.getCmp("serviceseach").setDisabled(true);
				    		}
				    	}
				    }
	            }]
	        }]
        }]
    });
});