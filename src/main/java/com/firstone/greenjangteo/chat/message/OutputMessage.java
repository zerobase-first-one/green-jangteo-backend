package com.firstone.greenjangteo.chat.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class OutputMessage {
    private LocalDateTime sentTime;
    private String content;
}
