/**
 * 添加项目路径
 */
Ext.define('projecttype', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
        {name: 'name', type: 'string'}
     ]
});
function addUrl(){
    
	var _$typesStore = Ext.create('Ext.data.ArrayStore', {
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
    _$typesStore.load();
    
    var formPanel = Ext.create('Ext.form.Panel', {
        bodyPadding: 5,
		border: 0,
		url: url+'/DataManage_sqliteOperate.action?type=1',
        fieldDefaults: {
            labelAlign: 'left',
            labelWidth: 90,
            anchor: '100%'
        },

        items: [{
            id:'S003',
            name:'S003',
            fieldLabel: '项目分类',
            xtype: 'combobox',
            queryMode: 'local',
		    store: _$typesStore,
            multiSelect: true,
		    displayField: 'name',
		    emptyText:'所有项目',
		    valueField: 'id',
		    listeners:{
	        	change: function(obj, newVal, oldVal){
	        		Ext.getCmp("save").setDisabled(!(Ext.getCmp("S001").getValue().replace(/[\s|\n]/g, "")!=""&&Ext.getCmp("S003").getValue()!=undefined&&Ext.getCmp("S003").getValue()!=""));
	        	}
	       	}
        },{
        	xtype:'textareafield',
        	name:'S006',
        	id:'S006',
        	fieldLabel:'功能/业务名称',
        	listeners:{
        		change: function(obj, newVal, oldVal){
        			Ext.getCmp("save").setDisabled(!(Ext.getCmp("S001").getValue().replace(/[\s|\n]/g, "")!=""&&Ext.getCmp("S003").getValue()!=undefined&&Ext.getCmp("S003").getValue()!=""));
        		}
        	}
        },{
        	xtype: 'textareafield',
            name: 'S001',
            id:'S001',
            fieldLabel: '项目修改路径',
            listeners:{
	        	change: function(obj, newVal, oldVal){
	        		Ext.getCmp("save").setDisabled(!(Ext.getCmp("S001").getValue().replace(/[\s|\n]/g, "")!=""&&Ext.getCmp("S003").getValue()!=undefined&&Ext.getCmp("S003").getValue()!=""));
	        	}
	       	}
        }]
    });
    
    var win = Ext.create('Ext.window.Window', {
        title: '添加项目路径',
        collapsible: true,
        animCollapse: true,
        maximizable: true,
        width: 580,
        height: 430,
        minWidth: 300,
        minHeight: 200,
        layout: 'fit',
        items: formPanel,
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            layout: {
                pack: 'center'
            },
            items: [{
            	id:'save',
                minWidth: 80,
                text: '保存',
                formBind: true, //only enabled once the form is valid
		        disabled: true,
		        handler: function() {
		            if (formPanel.isVisible()) {
		                formPanel.submit({
		                	waitTitle: "请稍候",
							waitMsg: '正在提交数据...',
							method: 'POST', 
		                    success: function(form, action) {
		                    	var _$result = action.result.result;
		                    	if(_$result=="1"){
		                    		win.hide();
		                    		getIframeObj("editarea").urlsStore.load();
		                    	}else{
		                    		Ext.Msg.alert('Failed', "信息保存失败！");
		                    	}
		                    },
		                    failure: function(form, action) {
		                        Ext.Msg.alert('Failed', action.result.exception);
		                    }
		                });
		            }
		        }
            },{
                minWidth: 80,
                text: '关闭',
                handler: function(){
                	win.destory();
                }
            }]
        }],
        listeners:{
        	resize: function(obj, width, height){
        		Ext.getCmp("S001").setHeight(height-100);
        		Ext.getCmp("S001").setWidth(width-22);
        	}
       	}
    });
    win.show();
}

var win = null;
function selectPage(){
    
    win = Ext.create('Ext.window.Window', {
        title: '选择业务表格配置页面',
        collapsible: true,
        animCollapse: true,
        maximizable: true,
        width: 580,
        height: 430,
        minWidth: 300,
        minHeight: 200,
        layout: 'fit',
        html:'<form id="formpz" target="alert_tmp" method="post" action="'+url+'/DataManage_operateSubmit.action?type=9" enctype="multipart/form-data"><table class="accordion"><tr height="26"><td width="170" align="right">请选择业务表格配置文件</td><td><input type="file" name="S001" onchange="checkFileExt(this)" class="text"/></td></tr><tr><td>&nbsp;</td><td>&nbsp;</td></tr></table></form>',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            layout: {
                pack: 'center'
            },
            items: [{
                text: '确定',
		        handler: function() {
		        	if($("#S001").val()==""){
		        		alert("请选择页面文件！");
		        		return false;
		        	}
		        	$("#formpz").submit();
		        }
            }]
        }]
    });
    win.show();
    
}

/**
 * 检查文件后缀是否是Jsp、html
 */
function checkFileExt(obj){
	var _$fileName = obj.value;
	if( !_$fileName.match(/.JSP|.jsp|.html|.HTML/i)){//在前面添加你想要的图片格式后缀，中间以“|”分开。
		alert('页面文件格式无效');
		var obj1 = obj;
		var obj2 = obj1.cloneNode();
		obj1.parentNode.replaceChild(obj2,obj1);
		return false;
	}
	return true;
}

function reloadIframe(id){
	if(win){
		win.hide();
	}
	getIframeObj("editarea").location = url+"/DataManage_read.action?url=/extjs/plugins/layout/pageconf/pageconf.jsp&pageUrl="+id;
}


/**
 * 报表调试窗口
 */
var _$configFields = 0;
Ext.define('Birt.debug.control',{
	debugWindow:null,
	debugStore:null,
	debugGrid:null,
	config:{
		tab045id:null
	},
	initComponent: function(){
		var me=this,
		tab045id = me.config.tab045id;

	    Ext.define('debug', {
	        extend: 'Ext.data.Model',
	        fields: [{name: 'type', type: 'string'},{name: 'content', type: 'string'},{name: 'date', type: 'string'}]
	    });
	    
	    
	    // 业务表字段加载工厂
	    me.debugStore = Ext.create('Ext.data.ArrayStore', {
	        model: 'debug',
	        remoteSort: true,
	        proxy: {
	            type: 'ajax',
	            url: url+'/GridData_sqliteData.action?type=9',
	            reader : {type : 'json',root : 'data.list'}
	        }
	    });
	    
	    me.debugGrid = Ext.create('Ext.grid.Panel', {
	        store: me.debugStore,
	        columns: [
		        {text: "信息类型", width: 80, sortable: false, dataIndex: 'type'},
		        {text: "信息内容", flex: 1, sortable: false, dataIndex: 'content'},
		        {text: "记录时间", width: 118, sortable: false, dataIndex: 'date'}
		    ],
	        stripeRows: true,
	        autoScroll: true,
	        viewConfig: {
	            forceFit: true
	        },
	        height:150,
	        split: true,
	        region: 'north'
	    });
	    
	    // define a template to use for the detail view
	    var bookTplMarkup = ['信息类型: {type}<br/>','记录时间: {date}<br/>','信息内容: {content}<br/>'];
	    var bookTpl = Ext.create('Ext.Template', bookTplMarkup);
	
	    // update panel body on selection change
	    me.debugGrid.getSelectionModel().on('selectionchange', function(sm, selectedRecord) {
	        if (selectedRecord.length) {
	            var detailPanel = Ext.getCmp('detailPanel');
	            bookTpl.overwrite(detailPanel.body, selectedRecord[0].data);
	        }
	    });
	    
		me.debugWindow = Ext.create('Ext.window.Window', {
	        title: '报表预览控制台',
	        collapsible: true,
	        maximizable: true,
	        width: 650,
	        height: 470,
	        minWidth: 500,
	        minHeight: 300,
	        border: false,
	        layout: 'border',
	        items: [me.debugGrid, {
	        	id: 'detailPanel',
                region: 'center',
                bodyPadding: 7,
                bodyStyle: "background: #ffffff;",
                html: '请选择查看的信息。'
	        }]
	    });
    },
    
    constructor: function(config) {
    	var me = this;
    	config = config || {};
		this.initConfig(config);
		return this;
	},    

	show: function(){
		var me = this;
		me.initComponent();
		me.load();
		me.debugWindow.show();
	},
	
	hide: function(){
		var me = this;
		me.debugWindow.hide();
	},
	
	load: function(){
		var me = this,
		tab045id = me.config.tab045id;

		var _$debugProxy = me.debugStore.getProxy();
	    _$debugProxy.extraParams["tab045id"] = tab045id;
	    me.debugStore.load();
	},
	
	reloadParams: function(params){
		var me=this,
		config = me.config;
		if(params){
			if(params.tab045id) config.tab045id = params.tab045id;
		}
	}
});