package edu.udacity.java.nano.chat;

import java.util.Objects;

/**
 * WebSocket message model
 */
public class Message {

    private String username;
    private String msg;
    private MessageType type;
    private int onlineCount;
    private String sessionId;

    public enum MessageType {
        ENTER, LEAVE, SPEAK
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", msg='" + msg + '\'' +
                ", type=" + type +
                ", onlineCount=" + onlineCount +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return onlineCount == message.onlineCount &&
                username.equals(message.username) &&
                msg.equals(message.msg) &&
                type == message.type &&
                sessionId.equals(message.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, msg, type, onlineCount, sessionId);
    }
}