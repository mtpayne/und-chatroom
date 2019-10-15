package edu.udacity.java.nano.chat;

import okhttp3.*;
import okio.ByteString;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketChatServerTest {

    private OkHttpClient okHttpClient;
    private Request request;
    private WebSocketListener webSocketListener;

    @Before
    public void setUp() throws Exception {

        okHttpClient = new OkHttpClient();

        request = new Request.Builder()
                .url("ws://localhost:8080/chat")
                .build();

        webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        okHttpClient.dispatcher().executorService().shutdown();
    }

    @Test
    public void webSocketTest() {

        WebSocket webSocket = okHttpClient.newWebSocket(request, webSocketListener);
        // Assert not null and socket has been created
        Assert.assertNotNull(webSocket);
        boolean sent = webSocket.send("Test send");
        // Assert message was sent
        Assert.assertTrue(sent);
        boolean closed = webSocket.close(1000, "Close message");
        // Assert socket was closed
        Assert.assertTrue(closed);
    }
}