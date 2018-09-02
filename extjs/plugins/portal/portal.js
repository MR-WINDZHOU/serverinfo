/**
 * @class Ext.app.Portal
 * @extends Object
 * A sample portal layout application class.
 */
// TODO: Fill in the content panel -- no AccordionLayout at the moment
// TODO: Fix container drag/scroll support (waiting on Ext.lib.Anim)
// TODO: Fix Ext.Tool scope being set to the panel header
// TODO: Drag/drop does not cause a refresh of scroll overflow when needed
// TODO: Grid portlet throws errors on destroy (grid bug)
// TODO: Z-index issues during drag

var IPListGrid = null;
var dialogWindow = null;
var IPListStore = null;
var formatWindow = null;
Ext.define('Ext.app.Portal', {

    extend: 'Ext.container.Viewport',

    getTools: function(){
        return [{
            xtype: 'tool',
            type: 'gear',
            handler: function(e, target, panelHeader, tool){
                var portlet = panelHeader.ownerCt;
                portlet.setLoading('Working...');
                Ext.defer(function() {
                    portlet.setLoading(false);
                }, 2000);
            }
        }];
    },

    initComponent: function(){
    	
    	/*******************************************************************
    	 * 常规操作
    	 *******************************************************************/
    	Ext.define('NormalOperation', {
	        extend: 'Ext.data.Model',
	        fields: [
	            {name: 'operation'},
	            {name: 'url', type:'string'},
	            {name: 'function', type:'string'},
	            {name: 'isoracle', type:'boolean'},
	            {name: 'type'}
	         ]
	    });
	    
	    operationStore = Ext.create('Ext.data.ArrayStore', {
	        model: 'NormalOperation',
	        proxy: {
	            type: 'ajax',
	            url: url+'/GridData_sqliteData.action?type=4',
	            reader : {
					type : 'json',
					root : 'data.list'
				}
	        }
	    });
	
		
	    operationGrid = Ext.create('Ext.grid.Panel', {
	    	store: operationStore,
	        columns: [
	            Ext.create('Ext.grid.RowNumberer'),
	            {text: "链接地址", flex: 1, sortable: false, sortshow: false, hidden: true, dataIndex: 'url'},
	            {text: "操作选项", flex: 1, sortable: true, dataIndex: 'operation'},
	            {text: "库连接状态", flex: 1, sortable: true, renderer: function(val,metadata,rec){
	            	if(rec.get("isoracle")){
		            	return IP==""?'<span style="color:red;">未连接</span>':'<span style="color:green;">已连接</span>';
	            	}else{
		            	return '<span style="color:gray;">无需连接</span>';
	            	}
	            }, dataIndex: 'state'}
	        ],
	        columnLines: true,
	        iconCls:'icon-grid',
	        border: 0,
	        width: 198,
	        listeners:{
	        	itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
	        		if(rec.get("isoracle")&&IP==""){
	       				alert("基层和机关的数据库未开启或未链接，请正确连接数据库后再操作！");
	       				return false;
	        		}
	        		if(rec.get("function")!=""){
	        			eval(rec.get("function"));
	        			
	        		}else{
	        			getIframeObj("editarea").location = url + rec.get("url");
	        		}
				}
	        }
	    });
    	
    	/*******************************************************************
    	 * IP列表
    	 *******************************************************************/
    	Ext.define('IPList', {
	        extend: 'Ext.data.Model',
	        fields: [
	            {name: 'companyid', type: 'string'},
	            {name: 'companyname', type: 'string'},
	            {name: 'companystatus', type: 'string'},
	            {name: 'remark', type: 'string'}
	         ]
	    });
	    // Array data for the grids
	    IPListStore = Ext.create('Ext.data.ArrayStore', {
	        model: 'IPList',
	        proxy: {
	            type: 'ajax',
	            url: url+'/GridDataZJF_readData.action?type=5',
	            reader : {
					type : 'json',
					root : 'data.list'
				}
	        }
	    });
	    
	    IPListGrid = Ext.create('Ext.grid.Panel', {
	    	store: IPListStore,
	        columns: [
	            {text: "序号", sortable: false, sortshow: false, dataIndex: 'companyid', width: 50},
	            {text: "客户名称", flex: 1, sortable: false, dataIndex: 'companyname'},
	            {text: "状态", sortable: false, renderer: function(val){return val=="1"?'<span style="color:green;">在用</span>':'<span style="color:red;">停用</span>';}, dataIndex: 'companystatus', width:40},
	            {text: "备注", sortable: false, dataIndex: 'remark'}
	            ],
	        columnLines: true,
	        border: 0,
	        dockedItems: [
	        {
	            xtype: 'toolbar',
	            items: [{
	                text:'新客户',
	                tooltip:'增加新的客户信息',
	                iconCls:'add_db',
	                handler: function(){
	                	createDialog('添加客户信息', '/function/ips/addcustomer.jsp', 200, 400);
	                }
	            },'-']
	         }],
	         listeners:{
		        	itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
		        		getIframeObj("editarea").location = url + "/extjs/plugins/layout/birtconf/config.jsp?companyid="+rec.get("companyid");
					}
		        }
	    });
    	
    	/*******************************************************************
    	 * 连接信息
    	 *******************************************************************/
    	Ext.define('connStatusInfo', {
	        extend: 'Ext.data.Model',
	        fields: [
	            {name: 'type'},
	            {name: 'info'}
	         ]
	    });
	    
	    // Array data for the grids
	    connStatusStore = Ext.create('Ext.data.ArrayStore', {
	        model: 'connStatusInfo',
	        proxy: {
	            type: 'ajax',
	            url: url+'/GridData_sqliteData.action?type=6',
	            reader : {
					type : 'json',
					root : 'data.list'
				}
	        }
	    });
	
	    connStatusGrid = Ext.create('Ext.grid.Panel', {
	    	store: connStatusStore,
	        columns: [
	            {text: "信息类型", sortable: false, dataIndex: 'type', width: 70},
	            {text: "信息内容", flex: 1, sortable: false, dataIndex: 'info'}
	        ],
	        columnLines: true,
	        border: 0,
	        dockedItems: [
	        {
	            xtype: 'toolbar',
	            items: [{
	                text:'断开连接',
	                tooltip:'断开连接',
	                iconCls:'disconn',
	                itemId: 'disConnButton',
	                disabled: IP=="",
	                handler: function(){
	                	if(IP==""){
	                		alert("连接已断开，不需断开！");
	                		return false;
	                	}
	                	if(saveSubmitAsyncNoAlert(url+"/IPManage_disConn.action", "type="+type)){
	                		IP = "";
	                		type = "";
	                		operationStore.load();
							IPListStore.load();
							connStatusStore.load();
							connStatusGrid.down('#disConnButton').setDisabled(true);
							getIframeObj("editarea").location = url +"/function/welcome/welcome.jsp";
						}
	                }
	            }]
	         }]
	    });
    
    	
        Ext.apply(this, {
            id: 'app-viewport',
            layout: {
                type: 'border',
                padding: '0 5 5 5' // pad the layout from the window edges
            },
            items: [{
                id: 'app-header',
                xtype: 'box',
                region: 'north',
                height: 40,
                html: '项目情况V1.0'
            },{
            	id: 'owj',
                xtype: 'container',
                region: 'center',
                layout: 'border',
                items: [{
                    id: 'app-options',
                    title: '操作选项',
                    region: 'west',
                    animCollapse: true,
                    width: 300,
                    minWidth: 150,
                    maxWidth: 400,
                    split: true,
                    collapsible: true,
                    layout: 'accordion',
                    layoutConfig:{
                        animate: true
                    },
                    items: [{
                        title:'所有客户',
                        autoScroll: true,
                        border: false,
                        iconCls: 'ips',
                        items: IPListGrid,
	                    listeners:{
		                	resize: function(obj, width, height){
		                		IPListGrid.setWidth(width);
		                		IPListGrid.setHeight(height-27);
		                	}
	                	}
                    }/*,{
                        title:'连接信息',
                        border: false,
                        autoScroll: true,
                        iconCls: 'conn',
                        items: connStatusGrid,
	                    listeners:{
		                	resize: function(obj, width, height){
		                		connStatusGrid.setWidth(width);
	                			connStatusGrid.setHeight(height-27);
		                	}
	                	}
                    },{
                        title:'常规操作',
                        autoScroll: true,
                        border: false,
                        iconCls: 'cof',
                        items: operationGrid,
	                    listeners:{
		                	resize: function(obj, width, height){
		                		operationGrid.setWidth(width);
		                		operationGrid.setHeight(height-27);
		                	}
	                	}
                    }*/]
                },{
                    id: 'app-portal',
                    region: 'center',
                    border: 0,
                    html:'<iframe id="editarea" name="editarea" src="'+url+'/function/welcome/welcome.jsp" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>'
                }]
            }]
        });
        this.callParent(arguments);
    	connStatusStore.load();
		operationStore.load();
		IPListStore.load();
    }
});

function clickRadio(th){
	if($(th).attr("value")==IP){
		IPListGrid.down('#connButton').setDisabled(true);
	}else{
		IPListGrid.down('#connButton').setDisabled(false);
	}
}

function createDialog(_$title, _$url, _$height, _$width){
	/*******************************************************************
   	 * 弹窗
   	 *******************************************************************/
   	if(dialogWindow){
   		dialogWindow=null;
   	}
   	dialogWindow = Ext.create('Ext.Window', {
        title: _$title,
        width: _$width,
        height: _$height,
        plain: true,
        bodyBorder:true,
        headerPosition: 'top',
        layout: 'fit',
        html:'<iframe src="'+url+_$url+'" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>'
    }).show();
}