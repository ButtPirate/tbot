package com.buttpirate.tbot.bot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends Exception {
    private Long chatId;

    public CustomException(Exception e) {
        super(e);
    }

    public CustomException(Exception e, Long chatId) {
        super(e);
        this.chatId = chatId;
    }

    public CustomException(String message, Long chatId) {
        super(message);
        this.chatId = chatId;
    }
}
