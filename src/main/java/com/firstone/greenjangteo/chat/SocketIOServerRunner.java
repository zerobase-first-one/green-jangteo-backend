package com.firstone.greenjangteo.chat;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SocketIOServerRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SocketIOServerRunner.class);

    @Value("${server.socket-io.url}")
    private String serverUrl;

    @Value("${server.socket-io.port}")
    private int serverPort;

    @Value("${spring.frontend.url}")
    private String frontendUrl;

    @Value("${spring.frontend.local.seunghak.url}")
    private String frontendLocalUrlForSeunghak;

    private static final String CONNECTED_LOG = "Client Connected: ";
    private static final String DISCONNECTED_LOG = "Client Disconnected: ";
    private static final String SEND_MESSAGE = "send_message";
    private static final String RECEIVE_MESSAGE = "receive_message";

    @Override
    public void run(String... args) throws Exception {
        Configuration config = new Configuration();
        config.setHostname(serverUrl);
        config.setPort(serverPort);

        config.setAuthorizationListener(handshakeData -> {
            String origin = handshakeData.getHttpHeaders().get("Origin");
            return frontendLocalUrlForSeunghak.contains("*") || (origin != null && origin.equals(frontendUrl));
        });

        final SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                log.info(CONNECTED_LOG + client.getSessionId());
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                log.info(DISCONNECTED_LOG + client.getSessionId());
            }
        });

        server.addEventListener(SEND_MESSAGE, String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                server.getBroadcastOperations().sendEvent(RECEIVE_MESSAGE, data);
            }
        });

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
        }));
    }
}