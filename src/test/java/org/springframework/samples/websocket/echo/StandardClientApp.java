/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.websocket.echo;

import java.io.IOException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.hs.spring.websocket.client.GreetingService;
import com.hs.spring.websocket.client.SimpleClientWebSocketHandler;
import com.hs.spring.websocket.client.SimpleGreetingService;

public class StandardClientApp {

	private static final String WS_URI = "ws://localhost:8080/spring-websocket-test/echo";


	public static void main(String[] args) throws IOException {
		try{
			AnnotationConfigApplicationContext cxt = new AnnotationConfigApplicationContext(ClientConfig.class);
			System.out.println("\n\n\nWhen ready, press any key to exit\n\n\n");
			System.in.read();
			cxt.close();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		finally {
			System.exit(0);
		}
	}

	@Configuration
	static class ClientConfig {

		@Bean
		public WebSocketConnectionManager wsConnectionManager() {

			WebSocketConnectionManager manager = new WebSocketConnectionManager(client(), handler(), WS_URI);
			manager.setAutoStartup(true);

			return manager;
		}

		@Bean
		public StandardWebSocketClient client() {
			return new StandardWebSocketClient();
		}

		@Bean
		public SimpleClientWebSocketHandler handler() {
			return new SimpleClientWebSocketHandler(greetingService());
		}

		@Bean
		public GreetingService greetingService() {
			return new SimpleGreetingService();
		}
	}

}
