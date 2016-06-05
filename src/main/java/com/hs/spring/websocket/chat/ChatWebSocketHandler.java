package com.hs.spring.websocket.chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatWebSocketHandler extends TextWebSocketHandler {
	private static final Map<String,Chat> userSocketSessionMap = new HashMap<String,Chat>();
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String userId = (String) session.getHandshakeAttributes().get("userId");
	        if (userSocketSessionMap.get(userId) == null) {
	        	Chat chat = new Chat(session);
	        	chat.startWS(userId);
	            userSocketSessionMap.put(userId, chat);
	        }
	        
	}

	
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		  System.out.println("Websocket:" + session.getId() + "已经关闭");
	        Iterator<Entry<String, Chat>> it = userSocketSessionMap.entrySet().iterator();
	        // 移除Socket会话
	        while (it.hasNext()) {
	            Entry<String, Chat> entry = it.next();
	            if (entry.getValue().getSession().getId().equals(session.getId())) {
	                userSocketSessionMap.remove(entry.getKey());
	                System.out.println("Socket会话已经移除:用户ID" + entry.getKey());
	                Chat chat =  entry.getValue();
	                if(chat!=null){
	                	chat.closeWS();
	                }
	                break;
	            }
	        }
	}

	

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String userId = (String) session.getHandshakeAttributes().get("userId");
		userSocketSessionMap.get(userId).receiveMsg(message.getPayload());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		  if (session.isOpen()) {
	            session.close();
	        }
	        Iterator<Entry<String, Chat>> it = userSocketSessionMap.entrySet().iterator();
	        // 移除Socket会话
	        while (it.hasNext()) {
	            Entry<String, Chat> entry = it.next();
	            if (entry.getValue().getSession().getId().equals(session.getId())) {
	            	entry.getValue().onError(exception);
	                userSocketSessionMap.remove(entry.getKey());
	                System.out.println("Socket会话已经移除:用户ID" + entry.getKey());
	                break;
	            }
	        } 
	}

}
