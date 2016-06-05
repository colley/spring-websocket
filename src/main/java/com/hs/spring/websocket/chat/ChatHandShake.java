/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.hs.spring.websocket.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


/**
 *@FileName  ChatHandShake.java
 *@Date  16-6-4 下午5:01
 *@author Colley
 *@version 1.0
 */

public class ChatHandShake implements HandshakeInterceptor {
    protected final Log logger = LogFactory.getLog(getClass());
    private static SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
    
    static{
    	Map<String,String> urlMap = new HashMap<String,String>();
    	urlMap.put("/chat/{userId}", "1");
    	urlMap.put("/chat/sockjs/{userId}", "2");
    	urlMap.put("/chat/sockjs/{userId}/**", "3");
    	simpleUrlHandlerMapping.setUrlMap(urlMap);
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest requests = ((ServletServerHttpRequest) request).getServletRequest();
            Map<String,String> params = lookupUriTemplateVariables(requests);
            // 标记用户
            String userId =params.get("userId");
            if (userId != null) {
                attributes.put("userId", userId);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Exception exception) {
        // TODO Auto-generated method stub
    }

    protected Map<String, String> lookupUriTemplateVariables(HttpServletRequest request) throws Exception {
        String urlPath = simpleUrlHandlerMapping.getUrlPathHelper().getLookupPathForRequest(request);
        Map<String, ?> urlMap = simpleUrlHandlerMapping.getUrlMap();
        List<String> matchingPatterns = new ArrayList<String>();
        for (String registeredPattern : urlMap.keySet()) {
            if (simpleUrlHandlerMapping.getPathMatcher().match(registeredPattern, urlPath)) {
                matchingPatterns.add(registeredPattern);
            }
        }

        String bestPatternMatch = null;
        if (!matchingPatterns.isEmpty()) {
            Collections.sort(matchingPatterns, simpleUrlHandlerMapping.getPathMatcher().getPatternComparator(urlPath));
            if (logger.isDebugEnabled()) {
                logger.debug("Matching patterns for request [" + urlPath + "] are " + matchingPatterns);
            }

            bestPatternMatch = matchingPatterns.get(0);
        }

        Map<String, String> uriTemplateVariables = simpleUrlHandlerMapping.getPathMatcher()
                                                                          .extractUriTemplateVariables(bestPatternMatch,
                urlPath);

        return uriTemplateVariables;
    }
}
