package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.CustomExceptionHandler;
import com.buttpirate.tbot.bot.exception.CustomException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

@Component
public class UpdateHandler {
    @Resource private ImportService importService;
    @Resource private ChatHandler chatHandler;
    @Resource private CallbackHandler callbackHandler;
    @Resource private CustomExceptionHandler exceptionHandler;

    public void handleUpdate(Update update) {
        Long chatId = null;

        try {
            // New post in channel
            if (update.hasChannelPost()) {
                importService.handleImport(update);

            // New message in chat with Bot
            } else if (update.hasMessage()) {
                chatId = update.getMessage().getChatId();
                chatHandler.handleMessage(update);

            // Callback
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
                callbackHandler.handleCallbackQuery(update);
            }

        } catch (CustomException e) {
            exceptionHandler.handleCustomException(e);
        } catch (Exception e) {
            exceptionHandler.handleException(e, chatId);
        }

    }

}
