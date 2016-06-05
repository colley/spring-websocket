//用于展示用户的聊天信息
Ext.define('MessageContainer', {

	extend : 'Ext.view.View',

	trackOver : true,

	multiSelect : false,

	itemCls : 'l-im-message',

	itemSelector : 'div.l-im-message',

	overItemCls : 'l-im-message-over',

	selectedItemCls : 'l-im-message-selected',

	style : {
		overflow : 'auto',
		backgroundColor : '#fff'
	},

	tpl : [
			'<div class="l-im-message-warn">​交谈中请勿轻信汇款、中奖信息、陌生电话。 请遵守相关法律法规。</div>',
			'<tpl for=".">',
			'<div class="l-im-message">',
			'<div class="l-im-message-header l-im-message-header-{source}">{from}  {timestamp}</div>',
			'<div class="l-im-message-body">{content}</div>',
			'</div>', '</tpl>' ],

	messages : [],

	initComponent : function() {
		var me = this;
		me.messageModel = Ext.define('Leetop.im.MessageModel',{
					extend : 'Ext.data.Model',
					fields : [ 'from', 'timestamp', 'content', 'source' ]
				});
		me.store = Ext.create('Ext.data.Store', {
			model : 'Leetop.im.MessageModel',
			data : me.messages
		});
		me.callParent();
	},

	// 将服务器推送的信息展示到页面中
	receive : function(message) {
		var me = this;
		message['timestamp'] = Ext.Date.format(new Date(message['timestamp']), 'H:i:s');
		if (message.from == userId) {
			message.source = 'self';
		} else {
			message.source = 'remote';
		}
		me.store.add(message);
		if (me.el.dom) {
			me.el.dom.scrollTop = me.el.dom.scrollHeight;
		}
	}
});

Ext.onReady(function() {
	var chatPanel = new Ext.Panel({
		region : 'center',
		layout : 'border',
		border: false,
		items : [ ]
	});
	
	var websocket, curCus;

	// 初始话WebSocket
	function initWebSocket() {
		var url = "";
		 if (window.location.protocol == 'http:') {
             url = 'ws://' + window.location.host + contentPath + "/chat/S23";
         } else {
             url = 'wss://' + window.location.host + contentPath + "/chat/S23";
         }
		if ('WebSocket' in window) {
			websocket = new WebSocket(url);
        } else if ('MozWebSocket' in window) {
        	websocket = new MozWebSocket(url);
        } else {
        	websocket = new SockJS(contentPath + "/chat/sockjs/S23");
        }
		websocket.onopen = function() {
			// 连接成功
			win.setTitle(title + '&nbsp;&nbsp;(已连接)');
		}
		websocket.onerror = function() {
			// 连接失败
			win.setTitle(title + '&nbsp;&nbsp;(连接发生错误)');
		}
		websocket.onclose = function() {
			// 连接断开
			win.setTitle(title + '&nbsp;&nbsp;(已经断开连接)');
		}
		// 消息接收
		websocket.onmessage = function(message) {
			 message = JSON.parse(message.data);
			// 接收用户发送的消息
			if (message.type == 'message') {
				var container = Ext.getCmp(message.cusId+"_output");
				container.receive(message);
			} else if (message.type == 'addCus') {
				// 创建新客户
				var input = new Ext.form.field.HtmlEditor({
					id: message.cusId+"_input",
					region : 'south',
					height : 105,
					enableFont : false,
					enableSourceEdit : false,
					enableAlignments : false,
					listeners : {
						initialize : function() {
							Ext.EventManager.on(me.input.getDoc(), {
								keyup : function(e) {
									if (e.ctrlKey === true && e.keyCode == 13) {
										e.preventDefault();
										e.stopPropagation();
										send();
									}
								}
							});
						}
					}
				});
				// 创建消息展示容器
				var output = new MessageContainer({
					id: message.cusId+"_output",
					region : 'center'
				});
				var chat = new Ext.Panel({
					id: message.cusId,
					region : 'center',
					layout : 'border',
					frame: true,
					border: false,
					items : [ input, output ],
					buttons : [ {
						text : '发送',
						handler : send
					} ]
				});
				chatPanel.add(chat);
				if(chatPanel.items.length==1){
					curCus = message.cusId;
				}else{
					chat.hide();
				}
				chatPanel.doLayout();
				// 列表
				var root = onlineUser.getRootNode();
				var node = root.createNode({
					id : message.cusId,
					text : message.cusId,
					iconCls : 'user',
					leaf : true
				});
				root.appendChild(node);
			} else if (message.type == 'user_leave') {
				// 用户下线
				var root = onlineUser.getRootNode();
				var user = message.user;
				var node = root.findChild('id', user);
				root.removeChild(node);
			}
		}
	}
	;

	// 在线用户树
	var onlineUser = new Ext.tree.TreePanel({
		title : '在线用户',
		rootVisible : false,
		region : 'east',
		width : 150,
		lines : false,
		useArrows : true,
		autoScroll : true,
		split : true,
		iconCls : 'user-online',
		store : Ext.create('Ext.data.TreeStore', {
			root : {
				text : '在线用户',
				expanded : true,
				children : []
			}
		}),
		tools: [{
			id: "minus",
			handler: function(){
				var nodes = onlineUser.getSelectionModel().getChecked();
				if(nodes.length==0){
					Ext.Msg.alert("信息提示", "请选择要关闭的客户！");
					return false;
				}
				var root = onlineUser.getRootNode();
				root.removeChild(nodes[0]);
				var chat = Ext.getCmp(nodes[0].id)
				chatPanel.remove(chat);
				chatPanel.doLayout();
			}
		}]
	});
	onlineUser.on("click", function(node, e){
		var chat = Ext.getCmp(curCus);
		chat.hide();
		chat = Ext.getCmp(node.id);
		chat.show();
	});
	var title = '欢迎您：' + user;
	// 展示窗口
	var win = new Ext.Window({
		title : title + '&nbsp;&nbsp;(未连接)',
		layout : 'border',
		iconCls : 'user-win',
		minWidth : 650,
		minHeight : 460,
		width : 650,
		animateTarget : 'websocket_button',
		height : 460,
		items : [ chatPanel, onlineUser ],
		border : false,
		listeners : {
			render : function() {
				initWebSocket();
			}
		}
	});
	win.show();

	// 发送消息
	function send() {
		var message = {};
		if (websocket != null) {
			var input = Ext.getCmp(curCus+"_input");
			if (input.getValue()) {
				Ext.apply(message, {
					from : userId,
					cusId : curCus,
					content : input.getValue()
				});
				websocket.send(JSON.stringify(message));
				input.setValue('');
			}
		} else {
			Ext.Msg.alert('提示', '您已经掉线，无法发送消息!');
		}
	}
});
