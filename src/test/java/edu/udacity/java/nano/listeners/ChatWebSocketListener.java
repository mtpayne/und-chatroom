package edu.udacity.java.nano.listeners;

import com.alibaba.fastjson.JSON;
import edu.udacity.java.nano.chat.Message;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.util.ArrayList;
import java.util.List;

public class ChatWebSocketListener extends WebSocketListener {

    // Store all message sent to this users chat session
    private List<Message> messages = new ArrayList<Message>();

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        // On onOpen a message is automatically sends
        // So we should now have our ENTER message type message
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // Store all messages sent to this users chat session
        Message msg = JSON.parseObject(text, Message.class);
        messages.add(msg);
    }
}
