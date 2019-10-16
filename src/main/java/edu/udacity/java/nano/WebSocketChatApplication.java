package edu.udacity.java.nano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;

@SpringBootApplication
@RestController
public class WebSocketChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketChatApplication.class, args);
    }

    /**
     * Login Page
     */
    @GetMapping("/")
    public ModelAndView login() {
        return new ModelAndView("/login");
    }

    /**
     * Chatroom Page
     */
    @GetMapping("/index")
    public ModelAndView index(String username, HttpServletRequest request) throws UnknownHostException {
        if(username == null || username.trim().isEmpty()) {
            // Since the placeholder is Username. We'll keep that as the default
            username = "Username";
        }
        // Set viewName and add username/webSocketUrl attributes for use
        // TODO: externalize url
        ModelAndView modelAndView = new ModelAndView("/chat");
        modelAndView.addObject("username", username);
        request.setAttribute("webSocketUrl", "ws://localhost:8080/chat/" + username);
        return modelAndView;
    }
}
