package com.buttpirate.tbot.bot;

import com.buttpirate.tbot.bot.configuration.BotConfig;
import com.buttpirate.tbot.bot.exception.CustomException;
import com.buttpirate.tbot.bot.service.ChatHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.Resource;
import java.util.Arrays;

import static com.buttpirate.tbot.bot.service.TranslationService.getPhrase;

@Slf4j
@Component
public class CustomExceptionHandler {
    @Resource private CustomBot bot;
    @Resource private ChatHandler chatHandler;
    @Resource private BotConfig config;

    @SneakyThrows
    public void handleException(Exception e, Long chatId) {
        log.error("Error: ", e);

        if (chatId != null) {
            SendMessage errorMessage = new SendMessage(chatId.toString(), getPhrase("fatal-error"));
            bot.execute(errorMessage);

            if (config.debugEnabled) {
                SendMessage stackTraceMessage = new SendMessage(chatId.toString(), e.getMessage()+"\n"+Arrays.toString(e.getStackTrace()));
                bot.execute(stackTraceMessage);

            }
        }

    }

    @SneakyThrows
    public void handleCustomException(CustomException e) {
        if (e.getChatId() != null) {
            SendMessage errorMessage = new SendMessage(e.getChatId().toString(), e.getMessage());
            bot.execute(errorMessage);
        }

        chatHandler.handleStartCommand(e.getChatId());
    }

}
