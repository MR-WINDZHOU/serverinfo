var Rec = null;
var View = null;
var Node = null;
var storedata = null;
Ext.require(['*']);

Ext.onReady(function() {
    var cw;
	Ext.define('treemodel', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {name: 'text',  type: 'string'},
	        {name: 'leaf',  type: 'boolean'},
	        {name: 'birt',  type: 'string'},
	        {name: 'type',  type: 'string'},
	        {name: 'node',  type: 'string'},
	        {name: 'drop',  type: 'boolean'},
	        {name: 'id',  type: 'string'}
	    ]
	});

    store1 = Ext.create('Ext.data.TreeStore', {
    	model: 'treemodel',
        proxy: {
            type: 'ajax',
            url: 'TreeData_tree.action?type=3'
        },
        root: {
            id: 'templet',
            text: '所有模板',
            expanded: true
        }
    });
    
    store2 = Ext.create('Ext.data.TreeStore', {
    	model: 'treemodel',
        proxy: {
            type: 'ajax',
            url: 'TreeData_tree.action?type=4'
        },
        root: {
            text: '所有环节及模板',
            id: 'business',
            expanded: true
        }
    });
    
    //模板管理树
    var tree1 = Ext.create('Ext.tree.Panel', {
        id: 'tree1',
        store: store1,
        useArrows: true,
       	border:0,
        menuInfo: false,
        ctxMenu:false,
        viewConfig: {
            plugins: {
                ptype: 'treeviewdragdrop',
                appendOnly: true
            },
            listeners: {
                beforedrop: function(node, data, dropRec, dropPosition){
                	return false;
                },
                itemmousedown: function (view, rec, item, index, e){
                	View = view;
                	Rec = rec;
                	storedata = "store1";
                },
				itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
					View = view;
					Rec = rec;
					storedata = "store1";
					if(rec.get("leaf")){
						getIframeObj("editarea").location = url+"/DataManage_read.action?url=/function/templet/templetmanage.jsp&table=TAB_4003&id="+rec.get("id");
					}else{
						var idtemp=rec.get("id").split("@")[0];
						if(idtemp=="templet"){
							idtemp="1";
						}
						getIframeObj("editarea").location = url+"/DataManage_read.action?url=/function/templet/groupmanage.jsp&table=TAB_4001&id="+idtemp;
					}
					view.expand(rec);//单击展开子节点
				},
		        itemcontextmenu: function(view, rec, node, index, e) {
		        	View = view;
		        	Rec = rec;
		        	Node = node;
		        	storedata = "store1";
		        	e.stopEvent();
		        	if(this.menuInfo){this.menuInfo.close();}
		        	if(rec.get("id")=='templet'){//根节点
						this.menuInfo = new Ext.menu.Menu({floating: true, items:[{
			        		text : '添加组',
			        		icon : url+'/extjs/plugins/shared/icons/menu/addgroup.png',
							handler : function() {
								getIframeObj("editarea").location = url+"/DataManage_read.action?url=/function/templet/groupmanage.jsp&parent="+rec.get("id").split("@")[0];
							}
			        	},'-',{
			        		text : '刷新',
			        		icon : url+'/extjs/plugins/shared/icons/menu/reload.png',
							handler : function() { 
								reloadNode();
							}
			        	}] });
					}else if(rec.get("leaf")){//叶子节点
						this.menuInfo = new Ext.menu.Menu({floating: true, items:[
						{
			        		text : '导出SQL',
			        		icon : url + '/extjs/plugins/shared/icons/menu/sql.png',
			        		handler : function(){
			        			alert("数据正准备导出，请及时查收，以免发生混淆！");
			        			getIframeObj("alert_tmp").location = url+"/ExportFile_exportTemplet.action?type=2&id="+rec.get("id");
			        		}
			        	},{
			        		text : '删除',
			        		menu : [{
			        			text: '删除不导出SQL',
			        			icon : url+'/extjs/plugins/shared/icons/menu/delete.png',
								handler : function() {
									if(deleteNoCloseOfURL(url+"/ExportFile_exportDelete.action", "export=0&type=1&id="+rec.get("id"))){
										getIframeObj("editarea").location = url+"/welcome.jsp";
										Rec = Rec.parentNode;
										reloadNode();
									}
								}
			        		},{
			        			text: '删除并导出SQL',
			        			icon : url+'/extjs/plugins/shared/icons/menu/sql.png',
								handler : function() {
									getIframeObj("editarea").location = url+"/welcome.jsp";
									alert("数据正准备导出，请及时查收，以免发生混淆！删除时可能数据不能正常刷新，请手动刷新！");
									getIframeObj("alert_tmp").location = url+"/ExportFile_exportDelete.action?export=1&type=1&id="+rec.get("id");
									Rec = Rec.parentNode;
									reloadNode();
								}
			        		}]
			        	},'-',{
			        		text : '刷新',
			        		icon : url+'/extjs/plugins/shared/icons/menu/reload.png',
							handler : function() { 
								reloadNode();
							}
			        	}] });
					}else{//非叶子、非根节点
						this.menuInfo = new Ext.menu.Menu({floating: true, items:[
						{
			        		text : '导出SQL',
			        		icon : url + '/extjs/plugins/shared/icons/menu/sql.png',
			        		handler : function(){
			        			alert("数据正准备导出，请及时查收，以免发生混淆！");
			        			getIframeObj("alert_tmp").location = url+"/ExportFile_exportTemplet.action?type=1&id="+rec.get("id").split("@")[0];
			        		}
			        	},{
			        		text : '添加',
			        		menu : [{
				        		text : '添加组',
				        		icon : url+'/extjs/plugins/shared/icons/menu/addgroup.png',
								handler : function() {
									var idtemp=rec.get("id").split("@")[0];
									if(idtemp=="templet"){
										idtemp="1";
									}
									getIframeObj("editarea").location = url+"/DataManage_read.action?url=/function/templet/groupmanage.jsp&parent="+idtemp;
								}
				        	},{
				        		text : '添加模板',
				        		icon : url+'/extjs/plugins/shared/icons/menu/addpage.png',
								handler : function() {
									getIframeObj("editarea").location = url+"/DataManage_read.action?url=/function/templet/templetmanage.jsp&parent="+rec.get("id").split("@")[0];
								}
				        	}]
			        	},'-',{
			        		text : '刷新',
			        		icon : url+'/extjs/plugins/shared/icons/menu/reload.png',
							handler : function() { 
								reloadNode();
							}
			        	}] });
					}
		        	this.menuInfo.showAt(e.getXY());
                    return false;
                }
            }
        }
    });
    
    //业务、环节、模版树
    var tree2 = Ext.create('Ext.tree.Panel', {
        id: 'tree2',
        useArrows: true,
        store: store2,
       	border:0,
        viewConfig: {
            plugins: {
                ptype: 'treeviewdragdrop',
                appendOnly: true
            },
            listeners: {
                beforedrop: function(node, data, dropRec, dropPosition){
                	if(dropPosition!="append"){
                		return false;
                	}
                	if(!data.records[0].get("leaf")){
                		return false;
                	}
                	if(!dropRec.get("drop")){
			   			return false;
			   		}
			   		if(saveSubmitAsyncNoAlert("DataManage_saveSubmitInfoAjax.action", "TAB_009_001="+data.records[0].get("id")+"&TAB_009_002="+dropRec.get("id").split("@")[0]+"&table=TAB_4002")){
			   			Rec.removeAll();
			   			Rec = Rec.parentNode;
			   			reloadNode();
			   			View = tree2.getView(node);
			   			Rec = dropRec;
			   			storedata = "store2";
			   			reloadNode();
			   			return true;
			   		}
			   		return false;
				},
				itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
					View = view;
					Rec = rec;
					storedata = "store2";
					if(rec.get("leaf")){
						getIframeObj("editarea").location = url+"/DataManage_read.action?url=/function/templet/templetmanage.jsp&table=TAB_4003&id="+rec.get("id").split("@")[0];
					}
					view.expand(rec);//单击展开子节点
				},
		        itemcontextmenu: function(view, rec, node, index, e) {
		        	View = view;
					Rec = rec;
					Node = node;
					storedata = "store2";
					e.stopEvent();
                    if(this.menuInfo){this.menuInfo.close();}
		        	if(rec.get("id")=='business'){
		        		this.menuInfo = new Ext.menu.Menu({floating: true, items:[{
			        		text : '刷新',
			        		icon : url+'/extjs/plugins/shared/icons/menu/reload.png',
							handler : function() {
								reloadNode();
							}
			        	}] });
		        	}else if(rec.get("leaf")){
			        	this.menuInfo = new Ext.menu.Menu({floating: true, items:[
			        	{
			        		text : '导出SQL',
			        		icon : url + '/extjs/plugins/shared/icons/menu/sql.png',
			        		handler : function(){
			        			alert("数据正准备导出，请及时查收，以免发生混淆！");
			        			getIframeObj("alert_tmp").location = url+"/ExportFile_exportTemplet.action?type=3&id="+rec.get("id").split("@")[0]+"&nodeid="+rec.get("node");
			        		}
			        	},{
			        		text : '删除',
			        		menu : [{
			        			text: '删除不导出SQL',
			        			icon : url+'/extjs/plugins/shared/icons/menu/delete.png',
								handler : function() {
									if(deleteNoCloseOfURL(url+"/ExportFile_exportDelete.action", "export=0&type=2&id="+rec.get("id").split("@")[0]+"&nodeid="+rec.get("node"))){
										Rec = Rec.parentNode;
										reloadNode();
									}
								}
			        		},{
			        			text: '删除并导出SQL',
			        			icon : url+'/extjs/plugins/shared/icons/menu/sql.png',
								handler : function() {
									alert("数据正准备导出，请及时查收，以免发生混淆！删除时可能数据不能正常刷新，请手动刷新！");
									getIframeObj("alert_tmp").location = url+"/ExportFile_exportDelete.action?export=1&type=2&id="+rec.get("id").split("@")[0]+"&nodeid="+rec.get("node");
									Rec = Rec.parentNode;
									reloadNode();
								}
			        		}]
			        	},'-',{
			        		text : '刷新',
			        		icon : url+'/extjs/plugins/shared/icons/menu/reload.png',
							handler : function() { 
								reloadNode();
							}
			        	}] });
		        	}else{
		        		this.menuInfo = new Ext.menu.Menu({floating: true, items:[
		        		{
			        		text : '导出SQL',
			        		icon : url + '/extjs/plugins/shared/icons/menu/sql.png',
			        		handler : function(){
			        			alert("数据正准备导出，请及时查收，以免发生混淆！");
			        			getIframeObj("alert_tmp").location = url+"/ExportFile_exportTemplet.action?type="+rec.get("type")+"&id="+rec.get("id").split("@")[0];
			        		}
			        	},{
			        		text : '刷新',
			        		icon : url+'/extjs/plugins/shared/icons/menu/reload.png',
							handler : function() {
								reloadNode();
							}
			        	}] });
		        	}
		        	this.menuInfo.showAt(e.getXY());
                    return false;
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
            title: '模板显示区',
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
                title: '编辑区域',
                region: 'center',
                collapseMode: 'mini',
                html:'<iframe id="editarea" name="editarea" src="'+url+'/welcome.jsp" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>'
            },{
                region: 'west',
                collapsible: true,
	            title: '业务、环节、模板',
	            split: true,
	            width: 210,
	            floatable: true,
                items:[tree2],
                listeners:{
		        	resize: function(obj, width, height){
		        		tree2.setWidth(width-2);
		        		tree2.setHeight(height-28);
		        	}
		       	}
            }]
        }]
    });
});

function reloadNode(){
	Rec.removeAll();//清空当前节点下的子节点 
	eval(storedata+".load({node:Rec,callback: function(){View.refresh();}})");
}