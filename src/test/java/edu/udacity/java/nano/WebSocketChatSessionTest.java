package edu.udacity.java.nano;

import com.alibaba.fastjson.JSON;
import edu.udacity.java.nano.chat.Message;
import edu.udacity.java.nano.listeners.ChatWebSocketListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static edu.udacity.java.nano.chat.Message.MessageType.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketChatSessionTest {

    private OkHttpClient okHttpClient;

    private Request studentRequest;
    private Request udacityRequest;
    private ChatWebSocketListener studentListener;
    private ChatWebSocketListener udacityListener;

    private final String userStudent = "Student";
    private final String userUdacity = "Udacity";

    @Before
    public void setUp() throws Exception {

        okHttpClient = new OkHttpClient();

        studentRequest = new Request.Builder()
                .url("ws://localhost:8080/chat/" + userStudent)
                .build();
        udacityRequest = new Request.Builder()
                .url("ws://localhost:8080/chat/" + userUdacity)
                .build();

        studentListener = new ChatWebSocketListener();
        udacityListener = new ChatWebSocketListener();
    }

    @After
    public void tearDown() throws Exception {
        okHttpClient.dispatcher().executorService().shutdown();
    }

    @Test
    public void webSocketChatSessionTest() throws InterruptedException {

        // Student will enter the chat session
        // Udacity will enter the chat session
        // Student will send a message
        // Student will exit chat

        // Student enters the chat
        WebSocket studentWebSocket = okHttpClient.newWebSocket(studentRequest, studentListener);

        // ENTER messages are sent automatically
        // Give time for messages to be sent and received
        TimeUnit.MILLISECONDS.sleep(500);

        // Lets validate the automatically generated ENTER message
        // Student has now entered the chat
        // WebSocketChatServer.onOpen automatically sends out an ENTER type message
        // So Student sent a message to Student
        // Message should be of type ENTER
        // Message has hardcoded value of "has entered the chat." = enterMsg
        // onlineCount is 1
        int onlineCount = 1;
        final String enterMsg = "has entered the chat.";

        // Get all the message that were sent to Student
        List<Message> studentMessages = studentListener.getMessages();

        // Get the ENTER message from Student to Student
        // This will return and remove from list the message with this username/type/onlineCount
        Message actualMsg = removeMessage(studentMessages, userStudent, ENTER, onlineCount);

        // Student's session id should remain consistent
        final String studentSessionId = actualMsg.getSessionId();

        // Create expected ENTER message from Student
        Message expectedMsg =
                createMessage(userStudent, ENTER, enterMsg, studentSessionId, onlineCount);

        // Assert they are the same
        assertEquals(expectedMsg, actualMsg);
        // Assert no more messages left
        assertEquals(0, studentMessages.size());



        // Udacity enters the chat
        WebSocket udacityWebSocket = okHttpClient.newWebSocket(udacityRequest, udacityListener);

        // ENTER messages are sent automatically
        // Give time for messages to be sent and received
        TimeUnit.MILLISECONDS.sleep(500);

        // Udacity now enters the chat
        // Websocket onOpen automatically sends a ENTER type message
        // So Udacity sent a message to Udacity
        // And Udacity sent a message to Student
        // Message should be of type ENTER
        // Message has hardcoded value of "has entered the chat." = enterMsg
        // onlineCount is now 2
        onlineCount = 2;

        // Get all the message that were sent to Udacity
        List<Message> udacityMessages = udacityListener.getMessages();

        // Get the ENTER message from Udacity to Udacity
        // This will return and remove from list the message with this username/type/onlineCount
        actualMsg = removeMessage(udacityMessages, userUdacity, ENTER, onlineCount);

        // Udacity's session id should remain consistent
        final String udacitySessionId = actualMsg.getSessionId();

        // Create expected ENTER message from Udacity
        expectedMsg =
                createMessage(userUdacity, ENTER, enterMsg, udacitySessionId, onlineCount);

        // Assert they are the same
        assertEquals(expectedMsg, actualMsg);
        // Assert no more messages left
        assertEquals(0, udacityMessages.size());

        // Get the ENTER message from Udacity to Student
        actualMsg = removeMessage(studentMessages, userUdacity, ENTER, onlineCount);

        // Expected ENTER message from Udacity should be same as above
        // Assert they are the same
        assertEquals(expectedMsg, actualMsg);
        // Assert no more messages left
        assertEquals(0, studentMessages.size());



        // Both are now in the chat
        // Student directly sends a message
        // Both Student and Udacity should receive SPEAK message
        final String studentSpeakMsg = "I have to go. Bye.";
        // TYPE, Session ID and online count will all be populated by application
        Message sendMessage =
                createMessage(userStudent, null, studentSpeakMsg, null, -1);
        studentWebSocket.send(JSON.toJSONString(sendMessage));

        // Give time for messages to be sent and received
        TimeUnit.MILLISECONDS.sleep(500);

        // So Student sent a message to Udacity
        // And Student sent a message to Student
        // Message should be of type SPEAK
        // Message has value of "I have to go. Bye." = studentMsg
        // onlineCount is still 2

        // Get all the message that were sent to Udacity
        udacityMessages = udacityListener.getMessages();

        // Get the SPEAK message from Student to Udacity
        // This will return and remove from list the message with this username/type/onlineCount
        actualMsg = removeMessage(udacityMessages, userStudent, SPEAK, onlineCount);

        // Create expected SPEAK message from Student
        expectedMsg =
                createMessage(userStudent, SPEAK, studentSpeakMsg, studentSessionId, onlineCount);

        // Assert they are the same
        assertEquals(expectedMsg, actualMsg);
        // Assert no more messages left
        assertEquals(0, udacityMessages.size());

        // Get the SPEAK message from Student to Student
        actualMsg = removeMessage(studentMessages, userStudent, SPEAK, onlineCount);

        // Expected SPEAK message from Student should be same as above
        // Assert they are the same
        assertEquals(expectedMsg, actualMsg);
        // Assert no more messages left
        assertEquals(0, studentMessages.size());



        // Student leaves the chat
        // Websocket onClose automatically sends a message
        // So Udacity should have received a message from Student
        // Message should be of type LEAVE
        // Message from Student has a hardcoded msg of
        //   "has left the chat." = studentLeaveMsg
        // onlineCount is now 1
        onlineCount = 1;
        final String studentLeaveMsg = "has left the chat.";
        studentWebSocket.close(1000, "Exiting Chat");

        // Give time for messages to be sent and received
        TimeUnit.MILLISECONDS.sleep(500);

        // Get all the message that were sent to Udacity
        udacityMessages = udacityListener.getMessages();

        // Get the LEAVE message from Student to Udacity
        // This will return and remove from list the message with this username/type/onlineCount
        actualMsg = removeMessage(udacityMessages, userStudent, LEAVE, onlineCount);

        // Create expected LEAVE message from Student
        expectedMsg =
                createMessage(userStudent, LEAVE, studentLeaveMsg, studentSessionId, onlineCount);

        // Assert they are the same
        assertEquals(expectedMsg, actualMsg);
        // Assert no more messages left
        assertEquals(0, udacityMessages.size());


        // Assert student didn't receive message
        assertEquals(0, studentMessages.size());
    }

    private Message createMessage(String username, Message.MessageType messageType, String msg, String sessionId, int onlineCount) {
        Message message = new Message();
        message.setUsername(username);
        message.setType(messageType);
        message.setMsg(msg);
        message.setSessionId(sessionId);
        message.setOnlineCount(onlineCount);

        return message;
    }

    // Will return and remove from list the message with this username/type/onlineCount
    private Message removeMessage(List<Message> messages, String username,
                                  Message.MessageType messageType, int onlineCount) {
        Message msg = null;
        for (Iterator<Message> it = messages.iterator(); it.hasNext() ;) {
            msg = it.next();
            if (msg.getUsername().equals(username)
                    && msg.getType().equals(messageType) && msg.getOnlineCount() == onlineCount) {
                it.remove();
                break;
            }
        }
        return msg;
    }

    private void printMessages(List<Message> messages, String username) {
        System.out.println(username + " message size : " + messages.size());
        for (Message message : messages) {
            System.out.println(username + " : " + JSON.toJSONString(message));
        }
    }
}
