package com.buttpirate.tbot.bot;

import com.buttpirate.tbot.bot.exception.CustomException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.Resource;

@Slf4j
@Component
public class CustomExceptionHandler {
    @Resource
    CustomBot bot;

    @SneakyThrows
    public void handleException(Exception e, Long chatId) {
        log.error("Error: ", e);

        if (chatId != null) {
            // TODO if debug enabled print stacktrace to chat
            SendMessage errorMessage = new SendMessage(chatId.toString(), "Fatal error!");
            bot.execute(errorMessage);
        }

    }

    @SneakyThrows
    public void handleCustomException(CustomException e) {
        if (e.getChatId() != null) {
            SendMessage errorMessage = new SendMessage(e.getChatId().toString(), e.getMessage());
            bot.execute(errorMessage);
        }
    }

}
