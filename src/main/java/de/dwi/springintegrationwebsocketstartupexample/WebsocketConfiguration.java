package de.dwi.springintegrationwebsocketstartupexample;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class WebsocketConfiguration {

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public WebSocketInboundChannelAdapter webSocketInboundChannelAdapter() {
        final var webSocketInboundChannelAdapter = new WebSocketInboundChannelAdapter(clientWebSocketContainer());
        webSocketInboundChannelAdapter.setOutputChannel(webSocketInboundChannel());
        webSocketInboundChannelAdapter.setErrorChannel(errorChannelForWebSocket());
        return webSocketInboundChannelAdapter;
    }

    @Bean
    public ClientWebSocketContainer clientWebSocketContainer() {
        final var container = new ClientWebSocketContainer(webSocketClient(), "ws://localhost:8080/ws");
        container.setConnectionTimeout(10);
        return container;
    }

    @Bean
    public MessageChannel webSocketInboundChannel() {
        return MessageChannels.publishSubscribe(Executors.newCachedThreadPool()).getObject();
    }

    @Bean
    public MessageChannel errorChannelForWebSocket() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    IntegrationFlow flow(final WebSocketInboundChannelAdapter webSocketInboundChannelAdapter) {
        return IntegrationFlow.from(webSocketInboundChannelAdapter).log().get();
    }
}
