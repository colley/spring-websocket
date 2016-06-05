package com.hs.spring.websocket.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class Chat {

    /**
     * 连接对象集合
     */
	private static final HashMap<String, Chat> cusChats = new HashMap<String, Chat>();
	private static final HashMap<String, Chat> serverChats = new HashMap<String, Chat>();
	private static final HashMap<String, Chat> waitChats = new HashMap<String, Chat>();
	private static final HashMap<String, String> csmap = new HashMap<String, String>();
	private static final HashMap<String, List<String>> scmap = new HashMap<String, List<String>>();
    private static final String CUSTOMER="customer";
    private static final String SERVER="server";
    private static final HashMap<String, Integer> serverNums = new HashMap<String, Integer>();  

    private String userId;
    private String type;

    /**
     * WebSocket Session
     */
    private WebSocketSession session;

    public Chat(WebSocketSession session) {
    	this.session = session;
    }
    
    

    public WebSocketSession getSession() {
		return session;
	}



	/**
     * 打开连接
     * 
     * @param session
     * @param nickName
     */
    public void startWS(String userId) {
    	this.userId = userId;
    	if(userId!=null && !"".equals(userId.trim())){
    		userId = userId.trim();
    		String serverId;
    		if(userId.startsWith("C")){
    			serverId = getServer();
        		String cusId = userId.substring(1);
        		type = CUSTOMER;
        		userId = cusId;
        		if(serverId==null || serverNums.get(serverId)>10){
        			waitChats.put(cusId, this);
        		}else{
        			connetServer(cusId, serverId, this);
        			int num = serverNums.get(serverId);
        			serverNums.put(serverId, num+1);
        		}
    		}else if(userId.startsWith("S")){
    			serverId = userId.substring(1);
        		type = SERVER;
        		userId = serverId;
        		serverChats.put(serverId, this);
        		int num = 0;
        		for (String cusId : waitChats.keySet()) {
        			connetServer(cusId, serverId, waitChats.get(cusId));
        			waitChats.remove(cusId);
        			num++;
					if(num==10)
						break;
				}
        		serverNums.put(serverId, num);
        		System.out.println("客服连接");
    		}
    	}
    }

    /**
     * 关闭连接
     */
    public void closeWS() {
    	if(CUSTOMER.equals(type)){
    		cusChats.remove(userId);
    		JSONObject json = new JSONObject();
    		json.put("content", "客户已经离线");
    		json.put("from", "system");
    		json.put("type", "message");
    		json.put("timestamp", (new Date()).getTime());
    		Chat.broadCast2User(csmap.get(userId), json.toString(), SERVER);
    		List<String> lstCus = scmap.get(csmap.get(userId));
    		if(lstCus!=null){
    			lstCus.remove(userId);
    		}
    		String serverId = csmap.get(userId);
    		scmap.put(serverId, lstCus);
    		csmap.remove(userId);
    		// 在等待中选择一个新的客户连接对应客服
    		String cusId = waitChats.keySet().iterator().next();
    		Chat chat = waitChats.get(cusId);
    		connetServer(cusId, serverId, chat);
    		waitChats.remove(cusId);
    	}else if(SERVER.equals(type)){
    		serverChats.remove(userId);
    		JSONObject json = new JSONObject();
    		json.put("content", "客服已经离线");
    		json.put("from", "system");
    		json.put("type", "message");
    		json.put("timestamp", (new Date()).getTime());
    		for (String cusId : scmap.get(userId)) {
				Chat.broadCast2User(cusId, json.toString(), CUSTOMER);
			}
    	}
    }

    /**
     * 接收信息
     * 
     * @param message
     * @param nickName
     */
    public void receiveMsg(String message) {
        System.out.println(message);
        if(session!=null && session.isOpen()){
        	 JSONObject msg =JSON.parseObject(message);
         	JSONObject json = new JSONObject();
         	json.put("content", msg.get("content"));
         	json.put("from", msg.get("from"));
         	json.put("cusId", msg.get("cusId"));
         	json.put("type", "message");
         	json.put("timestamp", (new Date()).getTime());
         	String cusId = msg.getString("cusId");
         	Chat.broadCast2User(cusId, json.toString(), CUSTOMER);
         	Chat.broadCast2User(csmap.get(cusId), json.toString(), SERVER);
        }
    }

    /**
     * 错误信息响应
     * 
     * @param throwable
     */
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    /**
     * 给具体用户发送或广播信息
     * @param message
     */
    private static void broadCast2User(String key, String message, String type) {
    	Chat chat = null;
    	if(CUSTOMER.equals(type)){
    		chat = cusChats.get(key);
    	}else{
    		chat = serverChats.get(key);
    	}
		try {
			if(chat == null){
				if(CUSTOMER.equals(type)){
		    		cusChats.remove(key);
		    	}else if(SERVER.equals(type)){
		    		serverChats.remove(key);
		    	}
				System.out.println(key+" is disconnect!");
				return;
			}
			synchronized (chat) {
				if(chat.session.isOpen()){
					chat.session.sendMessage(new TextMessage(message));
				}
			}
		} catch (IOException e) {
			if(CUSTOMER.equals(type)){
	    		cusChats.remove(key);
	    	}else if(SERVER.equals(type)){
	    		serverChats.remove(key);
	    	}
			try {
				if(chat!=null){
					if(chat.session.isOpen()){
						chat.session.close();
					}
				}
			} catch (IOException e1) {
			}
			System.out.println(key+" is disconnect!");
		}
    }
    
    private String getServer(){
    	String serverId = null;
    	int nums = Integer.MAX_VALUE;
    	for (String sid : serverNums.keySet()) {
			if(serverNums.get(sid)<nums)
				serverId = sid;
		}
    	return serverId;
    }
    
    private void connetServer(String cusId, String serverId, Chat chat){
    	cusChats.put(cusId, chat);
		csmap.put(cusId, serverId);
		List<String> lstCus = scmap.get(serverId);
		if(lstCus==null){
			lstCus = new ArrayList<String>();
		}
		lstCus.add(cusId);
		scmap.put(serverId, lstCus);
		JSONObject json = new JSONObject();
		json.put("content", "客服"+serverId+"为您服务");
        json.put("from", "system");
        json.put("type", "message");
        json.put("timestamp", (new Date()).getTime());
        Chat.broadCast2User(cusId, json.toString(), CUSTOMER);
        json = new JSONObject();
        json.put("cusId", cusId);
        json.put("type", "addCus");
        json.put("timestamp", (new Date()).getTime());
		Chat.broadCast2User(serverId, json.toString(), SERVER);
		json.put("content", "客户"+cusId+"已经连接");
		json.put("from", "system");
		json.put("type", "message");
		json.put("timestamp", (new Date()).getTime());
		Chat.broadCast2User(serverId, json.toString(), SERVER);
		System.out.println("客户连接");
    }
}