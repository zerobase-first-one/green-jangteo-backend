package com.firstone.greenjangteo.chat.controller;

import com.firstone.greenjangteo.chat.message.InputMessage;
import com.firstone.greenjangteo.chat.message.OutputMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat")
    @SendTo("/topic/message")
    public OutputMessage outputMessage(InputMessage inputMessage) {
        log.info("inputMessage: {}", inputMessage);

        return new OutputMessage(LocalDateTime.now(), inputMessage.getContent());
    }
}
