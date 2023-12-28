package com.firstone.greenjangteo.chat.message;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Getter
public class InputMessage {
    private String content;
}
