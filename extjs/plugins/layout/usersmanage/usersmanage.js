Ext.require(['*']);

Ext.onReady(function() {
    
    Ext.define('users', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'id', type: 'int'},
            {name: 'name', type: 'string'},
            {name: 'show', type: 'string'},
            {name: 'password', type: 'string'},
            {name: 'type', type: 'int'}
         ]
    });
    
    // 所有用户 加载工厂
    usersStore = Ext.create('Ext.data.ArrayStore', {
        model: 'users',
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
    
    usersGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
        	 plugins: {
                ddGroup: 'GridExample',
                ptype: 'gridviewdragdrop',
                enableDrop: false
            },
            listeners: {
				itemclick : function(view, rec, node) {//树节点单击时候记录下view、record、node
					userName.setValue(rec.get("name")+"");
		            userName.setDisabled(true);
		            userPwd.setValue(rec.get("password")+"");
		            userPwd.setDisabled(!manage&&author!=rec.get("id"));
		            showName.setValue(rec.get("show")+"");
		            showName.setDisabled(!manage&&author!=rec.get("id"));
		            userType.select(rec.get("type")+"");
		            userID.setValue(rec.get("id")+"");
		            Ext.getCmp("save").setDisabled(!manage&&author!=rec.get("id"));
				}
			}
        },
        store: usersStore,
        columns: [
        	Ext.create('Ext.grid.RowNumberer'),
	        {text: "显示名", flex: 1, sortable: true, dataIndex: 'show'},
	        {text: "用户名", flex: 1, sortable: true, dataIndex: 'name'},
	        {text: "用户类型", flex: 1, sortable: true, dataIndex: 'type', renderer:function(val){return (val==1?"管理":"普通")+"用户";}}
	    ],
        stripeRows: true,
        border: false,
        autoScroll: true
    });
    
    // 用户名字段
    var userName = Ext.create('Ext.form.field.Text', {
        fieldLabel : '用户名',
        name       : 'name'
        
    });
    //显示名字段
    var showName = Ext.create('Ext.form.field.Text', {
        fieldLabel : '显示名',
        name       : 'showname'
        
    });
    //密码字段
    var userPwd = Ext.create('Ext.form.field.Text', {
        fieldLabel : '密码',
        inputType:'password',
        name       : 'password'
    });
    //用户ID字段(隐藏)
    var userID = Ext.create('Ext.form.field.Hidden', {
        fieldLabel : '用户ID',
        name       : 'id'
    });

	// 用户类型 数据工厂
	var usertypes = Ext.create('Ext.data.Store', {    
		fields: ['abbr', 'name'],    
		data : [        
			{"abbr":"0", "name":"普通用户"},        
			{"abbr":"1", "name":"管理用户"}       
		]
	});
	// Create the combo box, attached to the states data store
	
	var userType = Ext.create('Ext.form.ComboBox', {    
		fieldLabel: '用户类型',
		store: usertypes,
		queryMode: 'local',
		displayField: 'name',
		disabled: !manage,
		valueField: 'abbr'
	});

    // Setup the form panel
    var formPanel = Ext.create('Ext.form.Panel', {
        region     : 'center',
        bodyStyle  : 'padding: 10px;',
        margins    : '0 0 0 3',
        border: false,
        items      : [
            userID,
            showName,
            userName,
            userPwd,
            userType
        ]
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
            title: '用户列表',
            split: true,
            width: 210,
            floatable: true,
            items: usersGrid,
	        listeners:{
	        	resize: function(obj, width, height){
	        		usersGrid.setWidth(width-2);
	        		usersGrid.setHeight(height-56);
	        	}
	       	},
	       	dockedItems: [{
	            xtype: 'toolbar',
	            items: [{
	                text:'添加用户',
	                tooltip:'添加用户',
	                iconCls:'adduser',
	                handler: function(){
	                	//添加用户时，将用户名、显示名清空，并设置用户类型和用户密码为默认值
	                	Ext.getCmp("save").setDisabled(false);
	                	userID.setValue("");
	                	userName.setValue("");
	                	userName.setDisabled(false);
	                	showName.setValue("");
	                	showName.setDisabled(false);
	                	userPwd.setValue("123456");
	                	userPwd.setDisabled(true);
			            userType.select("0");
	                }
	            }]
			}]
        },{
            region: 'center',
	        title: '编辑区域',
	        items: formPanel,
	        listeners:{
	        	resize: function(obj, width, height){
	        		formPanel.setWidth(width-2);
	        		formPanel.setHeight(height-54);
	        	}
	       	},
	        dockedItems: [{
	            xtype: 'toolbar',
	            items: [{
	            	id: 'save',
	                text:'保存',
	                tooltip:'保存',
	                iconCls:'save',
	                disabled: true,
	                handler: function(){
	                	var password = userPwd.getValue();
	                	var username = userName.getValue();
	                	var showname = showName.getValue();
	                	if(password==""||username==""||showname==""){
	                		alert("用户名或密码不能为空！");
	                		return false;
	                	}
	                	//新添加用户，要判断用户名是否重复
	                	if(userID.getValue()==""){
	                		//判断用户名是否重复
		                	if(existSqliteNoAlert("SYSTAB200", "CONDITION1=S001&CONDITIONVALUE1="+username)>0){
			        			alert("【"+username+"】已经使用，请选择其他用户名！");
			        			userName.setValue("");
			        			return false;
			        		}
		        		}
		        		//保存用户信息
	                	if(saveSubmitAsyncNoAlert(url+"/DataManage_saveSqliteAjax.action", "TABLE=SYSTAB200&ID="+userID.getValue()+"&S001="+username+"&S003="+userType.getValue()+"&S002="+password+"&S004="+showname)){
	                		//信息成功保存后，设置用户密码为可编辑并将用户名设置为不可编辑
	                		usersStore.load();
	                		userID.setValue(insertRecord);
				            userName.setDisabled(true);
				            userPwd.setDisabled(false);
	                		return false;
	                	}
	                }
	            }]
			}]
        }]
    });
    
    /****
    * Setup Drop Targets
    ***/

    // This will make sure we only drop to the view container
    var formPanelDropTargetEl =  formPanel.body.dom;

    var formPanelDropTarget = Ext.create('Ext.dd.DropTarget', formPanelDropTargetEl, {
        ddGroup: 'GridExample',
        notifyEnter: function(ddSource, e, data) {

            //Add some flare to invite drop.
            formPanel.body.stopAnimation();
            formPanel.body.highlight();
        },
        notifyDrop  : function(ddSource, e, data){
            var selectedRecord = ddSource.dragData.records[0];
            userName.setValue(selectedRecord.get("name")+"");
            userName.setDisabled(true);
            showName.setValue(selectedRecord.get("show")+"");
            showName.setDisabled(!manage&&author!=selectedRecord.get("id"));
            userPwd.setValue(selectedRecord.get("password")+"");
            userPwd.setDisabled(!manage&&author!=selectedRecord.get("id"));//是本人或管理员，则可编辑，否则不可编辑
            userType.select(selectedRecord.get("type")+"");
            userID.setValue(selectedRecord.get("id")+"");
            Ext.getCmp("save").setDisabled(!manage&&author!=selectedRecord.get("id"));
            return true;
        }
    });
});