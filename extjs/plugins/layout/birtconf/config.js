var Rec = null;
var View = null;
Ext.require(['*']);

var _$selectType = "";
var configStore = null;
var pasteRec = null;

var configRecord = '';
var business = '';
var edit = false;
var typesLoadFlag = false;//编辑类型是否加载标识 true=加载 false=未加载
var _$business = "";//上一个业务ID
var _$node = "";//上一个环节ID
var fieldFormat = null;
var formatsConfigStore = null;
var serverdetail = null;
var tab046id = 0;
var formatTable = "";//选中的转换字段对应表
Ext.onReady(function() {
	Ext.define('treemodel', {
		extend: 'Ext.data.Model',
		fields: [
		         {name: 'text',  type: 'string'},
		         {name: 'leaf',  type: 'boolean'},
		         {name: 'companyid',  type: 'string'},
		         {name: 'itemid', type: 'string'},
		         {name: 'id',  type: 'string'}
		         
		         ]
	});
    
    store = Ext.create('Ext.data.TreeStore', {
    	model: 'treemodel',
    	proxy: {type: 'ajax',url: 'TreeDataZJF_tree.action?companyid='+companyid},
    	root: {text: '所有客服项目分类以及项目',id: 'companyid11',expanded: true}
    	});
    
    Ext.define('fields', {
        extend: 'Ext.data.Model',
        remoteSort: true,
        fields: [
            {name: 'id', type: 'int'},
            {name: 'itemid', type: 'int'},
            {name: 'colname', type: 'string'},
            {name: 'datavalue', type: 'string'}/*,
            {name: 'order', type: 'int'},
            {name: 'ipAddr', type: 'string'},
            {name: 'username', type: 'string'},
            {name: 'password', type: 'string'},
            {name: 'backupinfor', type: 'string'},
            {name: 'transtype', type: 'int'},
            {name: 'transtype_ZH', type: 'string'},
            {name: 'isuse', type: 'boolean'},
            {name: 'transformat', type: 'string'},
            {name: 'before', type: 'int'},
            {name: 'sort', type: 'int'},
            {name: 'formatid', type: 'string'},
            {name: 'target', type: 'boolean'}*/
         ]
    });
    
    Ext.define('fieldstable', {extend: 'Ext.data.Model',remoteSort: true,fields: [{name: 'id', type: 'int'},{name: 'table', type: 'string'},{name: 'table_ZH', type: 'string'},{name: 'field', type: 'string'},{name: 'field_ZH', type: 'string'},{name: 'show', type: 'string'},{name: 'show_ZH', type: 'string'}]});
    
    Ext.define('transformat', {extend: 'Ext.data.Model',remoteSort: true,fields: [{name: 'id', type: 'int'},{name: 'name', type: 'string'},{name: 'type_ZH', type: 'string'},{name: 'view', type: 'string'}]});
    
    // 业务表字段加载工厂
    fieldStore = Ext.create('Ext.data.ArrayStore', {
        model: 'fieldstable',
        remoteSort: true,
        groupField: 'table_ZH',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_readData.action?type=1',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    
    // 报表已配置字段加载工厂
    configStore = Ext.create('Ext.data.ArrayStore', {
        model: 'fields',
        groupField: 'table_ZH',
        remoteSort: true,
        proxy: {
            type: 'ajax',
            url: url+'/GridDataZJF_readData.action?type=4',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        },
        listeners : {
        	load: function (store, records){
        		if(records){
		        	_$ddLoadFlag = true;
	        	}
        	}
        }
    });
    
    // 转换类型加载工厂
    typesFormatsStore = Ext.create('Ext.data.ArrayStore', {
        model: 'transformat',
        remoteSort: true,
        pageSize: 20,
        groupField: 'type_ZH',
        proxy: {
            type: 'ajax',
            url: url+'/GridData_readData.action?type=2',
            reader : {
				type : 'json',
				root : 'data.list'
			}
        }
    });
    
    //业务、环节、模版树
    tree1 = Ext.create('Ext.tree.Panel', {
        id: 'tree1',
        store: store,
        useArrows: true,
        width:'100%',
        height:'100%',
       	bodyStyle:'width:100%; height:100%; overflow:auto; position: absolute; top:0px; bottom:0px;',
       	border:0,
        menuInfo: false,
        ctxMenu:false,
        viewConfig: {
            listeners: {
				itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
		        	View = view;
		        	Rec = rec;
		        	console.log(node);
					if(rec.get("leaf")){
						var _$proxyConfig = configStore.getProxy();
						console.log(_$proxyConfig);
					    _$proxyConfig.extraParams["itemid"] = rec.get("id");
						configStore.load();
						getIframeObj("editarea").location = url+"/DataManage_read.action?url=/function/templet/serverDetail.jsp&table=TAB_4003&id="+rec.get("id");
					}
					view.expand(rec);//单击展开子节点
				},
		        itemcontextmenu: function(view, rec, node, index, e) {
		        	View = view;
		        	Rec = rec;
		        	e.stopEvent();
		        	if(this.menuInfo){this.menuInfo.close();}
					if(rec.get("leaf")){//叶子节点 加载菜单
						this.menuInfo = new Ext.menu.Menu({floating: true, items:[
						{
			        		text : '确认启用报表',
			        		icon : url + '/extjs/plugins/shared/icons/fam/start.png',
			        		disabled: rec.get("start")||rec.get("config")<=0,
			        		handler : function(){
			        			if(confirm("如果报表模板启用，则报表配置的数据将不可修改，是否确定使用？")){
			        				if(saveSubmitAsyncNoAlert(url+"/DataManage_saveSubmitInfoAjax.action", "table=TAB_4006&id="+rec.get("config")+"&TAB_045_005=1")){
			        					Rec = Rec.parentNode;
			        					reloadNode();
			        					edit = true;
			        				}
			        			}
			        		}
			        	},
						{
			        		text : '恢复编辑报表',
			        		icon : url + '/extjs/plugins/shared/icons/fam/restart.png',
			        		disabled: !rec.get("start")||rec.get("config")<=0,
			        		handler : function(){
			        			if(confirm("如果报表恢复编辑，将不能导出SQL脚本，如果要导出脚本，请重新启用报表即可，是否确定恢复编辑报表？")){
			        				if(saveSubmitAsyncNoAlert(url+"/DataManage_saveSubmitInfoAjax.action", "table=TAB_4006&id="+rec.get("config")+"&TAB_045_005=0")){
			        					Rec = Rec.parentNode;
			        					reloadNode();
			        					edit = false;
			        				}
			        			}
			        		}
			        	},
						{
			        		text : '清空报表内容',
			        		icon : url + '/extjs/plugins/shared/icons/fam/clear.png',
			        		disabled: rec.get("start")||rec.get("config")<=0,
			        		handler : function(){
			        			if(confirm("是否清空报表内容？")){
			        				if(saveSubmitAsyncNoAlert(url+"/DataManage_operate.action", "type=10&tab_045_id="+rec.get("config"))){
			        					Rec = Rec.parentNode;
			        					reloadNode();
			        					edit = false;
			        				}
			        			}
			        		}
			        	},'-',
			        	{
			        		text : '导出报表SQL',
			        		icon : url + '/extjs/plugins/shared/icons/menu/sql.png',
			        		disabled: !rec.get("start")||rec.get("config")<=0,
			        		handler : function(){
			        			alert("数据正准备导出，请及时查收，以免发生混淆！");
			        			getIframeObj("alert_tmp").location = url+"/ExportFile_export.action?type=1&configid="+rec.get("config");
			        		}
			        	},
			        	{
			        		text : '打印报表',
			        		icon : url + '/extjs/plugins/shared/icons/fam/print.png',
			        		disabled: rec.get("config")<=0,
			        		handler : function(){
			        			$("#flag").val("pdf");
			        			$("#ReportName").val(rec.get("birt").replace(".rptdesign",""));
			        			$("#birtParam").val("personIDs=-1&batch=-1&business="+rec.get("business")+"&node="+rec.get("node")+"&report="+rec.get("birt"));
			        			$("#form2").submit();
			        		}
			        	},'-',{
			        		text : '粘帖',
			        		icon : url + '/extjs/plugins/shared/icons/fam/copy.png',
			        		disabled: pasteRec==null||pasteRec==rec||pasteRec.get("birt")!=rec.get("birt"),
			        		handler : function(){
			        			if(rec.get("config")>0){
			        				if(!confirm("复制的内容将会替换现有的内容，你是否替换？")){
			        					return false;
			        				}
			        			}
			        			if(saveSubmitAsyncNoAlert(url+"/ReportConfig_operate.action", "type=2&TAB_045_ID="+pasteRec.get("config")+"&TAB_045_001="+rec.get("business")+"&TAB_045_002="+rec.get("node"))){
				        			pasteRec = null;
				        			Rec = Rec.parentNode;
				        			reloadNode();
			        			}
			        		}
			        	},{
			        		text : '复制',
			        		icon : url + '/extjs/plugins/shared/icons/fam/paste.png',
			        		disabled: !rec.get("start")||rec.get("config")<=0,
			        		handler : function(){
			        			pasteRec = rec;
			        		}
			        	},'-',{
			        		text : '筛选条件设置',
			        		icon : url + '/extjs/plugins/shared/icons/fam/filter.png',
			        		disabled: rec.get("config")<=0,
			        		handler : function(){
			        			parent.createDialog("筛选条件设置", "/DataManage_read.action?url=/extjs/plugins/layout/filterconf/filterconf.jsp&#63;edit="+rec.get("start")+"&configId="+rec.get("config"), parent.$("body").height()*0.8, 700);
			        		}
			        	},{
			        		text : '排序条件设置',
			        		icon : url + '/extjs/plugins/shared/icons/fam/sort.png',
			        		disabled: rec.get("config")<=0,
			        		handler : function(){
			        			parent.createDialog("排序条件设置", "/DataManage_read.action?url=/extjs/plugins/layout/sortconf/sort.jsp&#38;configId="+rec.get("config")+"&#38;edit="+rec.get("start"), parent.$("body").height()*0.8, 800);
			        		}
			        	},'-',{
			        		text : '报表控制台',
			        		icon : url + '/extjs/plugins/shared/icons/fam/konsole.png',
			        		disabled: rec.get("config")<=0,
			        		handler : function(){
			        			parent.debugControl.reloadParams({tab045id:rec.get("config")});
			        			parent.debugControl.show();
			        		}
			        	},'-',{
			        		text : '刷新',
			        		icon : url+'/extjs/plugins/shared/icons/menu/reload.png',
							handler : function() { 
								reloadNode();
							}
			        	}] });
					}else{//非叶子节点
						if(rec.get("type")=="5"){//业务节点
							this.menuInfo = new Ext.menu.Menu({floating: true, items:[{
				        		text : '导出报表SQL',
				        		icon : url + '/extjs/plugins/shared/icons/menu/sql.png',
				        		handler : function(){
				        			alert("数据正准备导出，请及时查收，以免发生混淆！");
				        			getIframeObj("alert_tmp").location = url+"/ExportFile_export.action?type=2&business="+rec.get("id").split("@")[0];
				        		}
				        	},'-',{text : '刷新',icon : url+'/extjs/plugins/shared/icons/menu/reload.png',handler : function() { reloadNode();}}] });
						}else if(rec.get("type")=="4"){//环节节点
							this.menuInfo = new Ext.menu.Menu({floating: true, items:[{
				        		text : '导出报表SQL',
				        		icon : url + '/extjs/plugins/shared/icons/menu/sql.png',
				        		handler : function(){
				        			alert("数据正准备导出，请及时查收，以免发生混淆！");
				        			getIframeObj("alert_tmp").location = url+"/ExportFile_export.action?type=3&business="+rec.get("id").split("@")[1]+"&node="+rec.get("id").split("@")[0];
				        		}
				        	},'-',{text : '刷新',icon : url+'/extjs/plugins/shared/icons/menu/reload.png',handler : function() { reloadNode();}}] });
						}else{//类别节点以及根节点
							this.menuInfo = new Ext.menu.Menu({floating: true, items:[{text : '刷新',icon : url+'/extjs/plugins/shared/icons/menu/reload.png',handler : function() { reloadNode();}}] });
						}
					}
		        	this.menuInfo.showAt(e.getXY());
                    return false;
                }
            }
        }
    });
    
    // 待转移的字段 网格布局
    fieldsGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup'
            }
        },
        store: fieldStore,
        columns: [
	        {text: "栏目名称", flex: 1, sortable: false, dataIndex: 'field_ZH'},
	        {text: "栏目英文名", flex: 1, sortable: false, dataIndex: 'field'},
	        {text: "显示类型", width: 70, sortable: false, dataIndex: 'show_ZH'}
	    ],
	    features: [Ext.create('Ext.grid.feature.Grouping',{
	        groupHeaderTpl: '{name} (共 {rows.length} 个字段)',
	        menushow: false
	    })],
        stripeRows: true,
        height: '100%',
        border: false,
        autoScroll: true
    });
    
    // 报表字段配置 网格布局
    configGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdropnodeletesource',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                beforedrop: function(node, data, dropRec, dropPosition) {
                	if(edit){
                		alert("报表已使用，不可编辑！");
                		return false;
                	}
                	var _$placeOrder = dropRec?dropRec.get("id"):"0";
                	if(dropPosition=="before"){
                		_$placeOrder = dropRec?dropRec.get("before"):"0";
                	}
                	if(data.records[0].get("target")){//调整顺序
                		if(saveSubmitAsyncNoAlert(url+"/ReportConfig_changeOrders.action", "before="+data.records[0].get("id")+"&after="+_$placeOrder)){
		               		configStore.load();
                		}else{
                			return false;
                		}
                	}else{//插入
                		var _$params = "TAB_045_ID="+configRecord+"&TAB_046_001="+data.records[0].get("table")+"&TAB_046_002="+data.records[0].get("field")+"&after="+_$placeOrder;
                		if(saveSubmitAsyncNoAlert(url+"/ReportConfig_insertField.action", _$params)){
		               		configStore.load();
                		}else{
                			return false;
                		}
                	}
                	return true;
                },
		        itemcontextmenu: function(view, rec, node, index, e) {
		        	View = view;
					Rec = rec;
					e.stopEvent();
                    if(this.menuInfo){this.menuInfo.close();}
	        		this.menuInfo = new Ext.menu.Menu({floating: true, items:[
	        		{
	        			text: '编辑转换格式',
	        			icon : url+'/extjs/plugins/shared/icons/fam/plugin_edit.png',
						handler : function() {
							tab046id = rec.get("id");
							
							Ext.getCmp("htmlEditor").setDisabled(edit);
						    Ext.getCmp("htmlEditor").setValue(fieldFormats(tab046id));
						    
							Ext.getCmp("formatsave").setDisabled(edit);
							Ext.getCmp("formatclear").setDisabled(edit);
							Ext.getCmp("formatreset").setDisabled(edit);
							var _$formatsConfigProxy = formatsConfigStore.getProxy();
						    _$formatsConfigProxy.extraParams["tab046"] = tab046id;
						    formatsConfigStore.load();
						    
						    Ext.getCmp("fieldformat").setTitle(rec.get("table_ZH")+"->"+rec.get("field_ZH")+"("+rec.get("field")+")"+" 字段转换格式配置");
						    
						    formatTable = rec.get("table");
						    
						    Ext.getCmp('fieldformat').expand();// : w.collapse();
						}
	        		},'-',
	        		{
	        			text: '删除',
	        			icon : url+'/extjs/plugins/shared/icons/menu/delete.png',
	        			disabled: edit,
						handler : function() {
							if(deleteNoCloseOfURL(url+"/DataManage_operate.action", "type=4&id="+rec.get("id"))){
								configStore.load();
							}
						}
	        		}
		        	] });
		        	this.menuInfo.showAt(e.getXY());
                    return false;
                }
            }
        },
        store: configStore,
        /*features: [Ext.create('Ext.grid.feature.Grouping',{
	        groupHeaderTpl: '{name} (已选 {rows.length} 个字段)',
	        menushow: false
	    })],*/
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "itemid",  sortable: false, dataIndex: 'itemid'},
	        {text: "colname", sortable: false, dataIndex: 'colname'},
	        {text: "datavalue", width:200, sortable: false, dataIndex: 'datavalue'}//,
	        //{text: "order",  sortable: false, dataIndex: 'order'}
	        //, renderer: function(val,metadata,record){return '<div class="transformat" record="'+record.get("id")+'" transtype="'+record.get("transtype")+'">'+(val?val.replace(/(\{format[0-9]+\})/g,"<img src='"+url+"/extjs/plugins/shared/icons/fam/information.png' title='$1'/>"):"")+'</div>';}
	    ],
        stripeRows: true,
        border: false,
        autoScroll: true
    });
    
    //转换类型及转换格式 网格布局
    typesFormatsGrid = Ext.create('Ext.grid.Panel', {
    	viewConfig: {
        	plugins: {
                ddGroup: 'formatdrop',
                ptype: 'gridviewdragdrop',
                enableDrop: false
            },
            listeners: {
				itemmouseenter: function(view, e){
					if(_$ddLoadFlag&&!edit){
						initDD();
					}
				}
            }
        },
        features: [Ext.create('Ext.grid.feature.Grouping',{
	        groupHeaderTpl: '{name} (共 {rows.length} 种转换格式)',
	        menushow: false
	    })],
        store: typesFormatsStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "转换格式", flex: 1, sortable: true, dataIndex: 'name'},
	        {text: "预期效果", flex: 1, sortable: false, dataIndex: 'view'}
	    ],
        stripeRows: true,
        border: false,
        autoScroll: true
    });
    
   	//字段转换格式 数据加载工厂
    formatsConfigStore = Ext.create('Ext.data.ArrayStore', {
        model: 'fields',remoteSort: true,proxy: {type: 'ajax',url: url+'/GridData_readData.action?type=5',reader : {type : 'json',root : 'data.list'}},
        listeners : {
        	load: function (store, records){
        		if(records){
		        	_$ddLoadFlag = true;
	        	}
        	}
        }
    });
    
    //字段转换格式配置 网格布局
    formatsConfigGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdropnodeletesource',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                beforedrop: function(node, data, dropRec, dropPosition) {
	            	if(edit){
	            		alert("报表已启用，不可编辑！");
	            		return false;
	            	}
	            	if(formatTable==""){
	            		alert("请先选中编辑的字段！");
	            		return false;
	            	}
	            	if(formatTable!=data.records[0].get("table")){
	            		alert("字段转换格式只能在相同表之间进行！");
	            		return false;
	            	}
               		var _$params = "type=2&TAB_046_ID="+tab046id+"&TAB_045_ID="+configRecord+"&TAB_046_001="+data.records[0].get("table")+"&TAB_046_002="+data.records[0].get("field");
               		if(!saveSubmitAsyncNoAlert(url+"/ReportConfig_insertField.action", _$params)){
               			return false;
               		}
               		formatsConfigStore.load();
                	return true;
                },
		        itemcontextmenu: function(view, rec, node, index, e) {
		        	View = view;
					Rec = rec;
					e.stopEvent();
                    if(this.menuInfo){this.menuInfo.close();}
	        		this.menuInfo = new Ext.menu.Menu({floating: true, items:[
	        		{
	        			text: '使用',
	        			icon : url+'/extjs/plugins/shared/icons/fam/insert-text.png',
	        			disabled: edit,
						handler : function() {
							var _$temp = Ext.getCmp("htmlEditor").getValue();
							Ext.getCmp("htmlEditor").setValue(_$temp+"<IMG name={format"+rec.get("formatflag")+"} src='"+url+"/extjs/plugins/shared/icons/fam/information.png'/>")
						}
	        		},{
	        			text: '删除',
	        			icon : url+'/extjs/plugins/shared/icons/menu/delete.png',
	        			disabled: edit,
						handler : function() {
							if(!confirm("你确定删除该配置字段？")){
								return false;
							}
							var editorVal = Ext.getCmp("htmlEditor").getValue();
		                	editorVal = editorVal.replace(/\<IMG name=\{format([0-9]+)\} src=[\"|\']{1}(.|\s){1,30}\/extjs\/plugins\/shared\/icons\/fam\/information.png[\"|\']{1}\>/g,"{format$1}");
		                	editorVal = editorVal.replace("{format"+rec.get("formatid")+"}","")
		                	if(saveSubmitAsyncNoAlert(url+"/DataManage_operate.action", "type=3&TAB_046_ID="+tab046id+"&TAB_046_ID_01="+rec.get("id")+"&TAB_046_005="+editorVal)){
		                		formatsConfigStore.load();
		                		formatsConfigStore.load();
			                	Ext.getCmp("htmlEditor").setValue(editorVal.replace(/(\{format[0-9]+\})/g,"<img name=$1 src='"+url+"/extjs/plugins/shared/icons/fam/information.png'/>"))
		                	}
						}
	        		}] });
		        	this.menuInfo.showAt(e.getXY());
                    return false;
                }
            }
        },
        store: formatsConfigStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "栏目名称", flex: 1, sortable: false, dataIndex: 'field_ZH'},
	        {text: "栏目英文名", flex: 1, sortable: false, dataIndex: 'field'},
	        {text: "显示方式", width: 70, sortable: false, dataIndex: 'show_ZH'},
	        {text: "转换", width: 36, sortable: false, dataIndex: 'odata',renderer: function(val,metadata, rec){return '<input type="checkbox" name="'+rec.get("id")+'" value="1" '+(val?"checked=true":"")+' onclick="changeOdata(this, '+rec.get("id")+')" '+(rec.get("show")=="0"||edit?"disabled=true":"")+'/>';}},
	        {text: "转换格式", flex: 1, sortable: false, dataIndex: 'transformat', renderer: function(val,metadata,record){return '<div class="fieldTransformat" record="'+record.get("formatflag")+'" formatid="'+record.get("formatid")+'">'+(val?val.replace(/(\{format[0-9]+\})/g,"<img src='"+url+"/extjs/plugins/shared/icons/fam/information.png' alt='$1' name='$1'/>"):"")+'</div>';}}
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
            region: 'west',
            collapsible: true,
            title: '业务、环节、模板',
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
            layout: 'border',
            items:[{
	            region: 'west',
	            collapsible: true,
	            title: '服务器详情',
	            split: true,
	            width: 410,
	            floatable: true,
	            items: configGrid,
		        listeners:{
		        	resize: function(obj, width, height){
		        		configGrid.setWidth(width-2);
		        		configGrid.setHeight(height-28);
		        	}
		       	}
	        },{
	            region: 'center',
	            border: 0,
	            layout: 'border',
                collapseMode: 'mini',
	            items:[{
	                title: '报表字段配置区域',
	                region: 'center',
	                html:'<iframe id="editarea" name="editarea" src="'+url+'/welcome.jsp" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>',
	                /*items: configGrid,*/
	                listeners:{
			        	resize: function(obj, width, height){
			        		configGrid.setWidth(width-2);
			        		configGrid.setHeight(height-28);
			        	}
			       	}
	            }]
	        }/*,{
	            region: 'east',
	            title: '转换格式',
	            split:true,
	            width: 200,
	            minSize: 175,
	            maxSize: 400,
	            collapsible: true,
	            layoutConfig:{animate:true},
	            items: typesFormatsGrid,
	            listeners: {
	            	resize: function(obj, width, height){
	            		typesFormatsGrid.setWidth(width-2);
	            		typesFormatsGrid.setHeight(height-28);
	            	}
	            }
	        }*/]
        }]
    });
});

/**
 * 重新加载节点
 */
function reloadNode(){
	Rec.removeAll();//清空当前节点下的子节点 
	eval("store.load({node:Rec,callback: function(){View.refresh();}})");
}

/**
 * 初始化拖动效果(转换类型拖动到字段)
 */
var _$ddLoadFlag = false;
function initDD(){
	if(_$ddLoadFlag){//判断是否已经加载
		_$ddLoadFlag = false;
		//字段的拖动
		$(".transformat").each(function(i){
			var _$id = new Date().pattern("yyyyMMddhhmmssS")+""+i;
			$(this).parent().parent().attr("id", _$id);

			/****
		    * Setup Drop Targets
		    ***/
		    Ext.create('Ext.dd.DropTarget', document.getElementById(_$id), {
		        ddGroup: 'formatdrop',
		        notifyDrop  : function(ddSource, e, data){
					var selectedRecord = ddSource.dragData.records[0];
					if(saveSubmitAsyncNoAlert(url+"/ReportConfig_fieldFormat.action", "TAB_046_ID="+$(this.getEl()).find("div.transformat").attr("record")+"&TAB_048_ID="+selectedRecord.get("id"))){
						configStore.load();
			            return true;
					}else{
			            return false;
			        }
		        }
		    });
		});
		//转换字段的拖动
		$(".fieldTransformat").each(function(i){
			var _$id = new Date().pattern("yyyyMMddhhmmssS")+""+($(".fieldTransformat").length+i);
			$(this).parent().parent().attr("id", _$id);
			/****
		    * Setup Drop Targets
		    ***/
		    Ext.create('Ext.dd.DropTarget', document.getElementById(_$id), {
		        ddGroup: 'formatdrop',
		        notifyDrop  : function(ddSource, e, data){
					var selectedRecord = ddSource.dragData.records[0];
					if(saveSubmitAsyncNoAlert(url+"/DataManage_operate.action", "type=7&id="+$(this.getEl()).find("div.fieldTransformat").attr("formatid")+"&TAB_048_ID="+selectedRecord.get("id"))){
						formatsConfigStore.load();
						Ext.getCmp("htmlEditor").setValue(fieldFormats(tab046id));
			            return true;
					}else{
			            return false;
			        }
		        }
		    });
		});
	}
}

/**
 * 关闭窗口
 */
function closePage(){
	configStore.load();
}

/**
 * 获得字段的额转换格式
 * 参数：
 *   _$TAB046ID	: 字段配置表中的字段ID
 * 进行操作：无提示 返回记录个数
 */
function fieldFormats(_$TAB046ID){
	var _$records = "";
	$.ajax({
		type: "POST",
		url: url+"/DataManage_operate.action",
		data: "type=2&TAB046="+_$TAB046ID,
		async: false,
		dataType: "json",
        dataFilter: function(data, type) { 
            return data;
        },
		success:function(data){
			var result=data.result;
			if(result==1) _$records = data.format;
			return true;
		}
	});
	//判断字段是否存在转换格式,如果存在，将字符串中的特殊字符转换为图片显示
	if(_$records!=""){
		_$records = _$records.replace(/(\{format[0-9]+\})/g,"<img name=$1 src='"+url+"/extjs/plugins/shared/icons/fam/information.png'/>");
	}
	return _$records;
}