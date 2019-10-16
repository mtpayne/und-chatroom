package edu.udacity.java.nano.chat;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static edu.udacity.java.nano.chat.Message.MessageType.ENTER;
import static edu.udacity.java.nano.chat.Message.MessageType.LEAVE;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session   WebSocket Session
 */
@Component
@ServerEndpoint(
        value = "/chat/{username}",
        encoders = MessageEncoder.class)
public class WebSocketChatServer {

    /**
     * All chat sessions.
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    private static void sendMessageToAll(Message msg) {
        Iterator<String> keys = onlineSessions.keySet().iterator();
        synchronized (onlineSessions)
        {
            while(keys.hasNext()) {
                try {
                    // TODO : remove comment
                    //System.out.println("Sending msg : " + msg);
                    onlineSessions.get(keys.next()).getBasicRemote().sendObject(msg);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Open connection, 1) add session, 2) add user.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        // Add session to all chat sessions
        onlineSessions.put(session.getId(), session);

        // Create message and send to add user to chat
        Message message = new Message();
        message.setUsername(username);
        message.setMsg("has entered the chat.");
        message.setType(ENTER);
        message.setOnlineCount(onlineSessions.size());
        message.setSessionId(session.getId());

        sendMessageToAll(message);
    }

    /**
     * Send message, 1) get username and session, 2) send message to all.
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        // Create message from json to get username. Then update message
        Message message = JSON.parseObject(jsonStr, Message.class);
        message.setType(Message.MessageType.SPEAK);
        message.setOnlineCount(onlineSessions.size());
        message.setSessionId(session.getId());

        sendMessageToAll(message);
    }

    /**
     * Close connection, 1) remove session, 2) update user.
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) throws IOException {
        // Remove this session for all chat sessions
        onlineSessions.remove(session.getId());

        // Create message to give notice that user has left chat session
        Message message = new Message();
        message.setUsername(username);
        message.setType(LEAVE);
        message.setMsg("has left the chat.");
        message.setOnlineCount(onlineSessions.size());
        message.setSessionId(session.getId());

        sendMessageToAll(message);

        // Close this session
        session.close();
    }

    /**
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

}
