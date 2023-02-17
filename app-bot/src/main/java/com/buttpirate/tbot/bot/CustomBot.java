package com.buttpirate.tbot.bot;

import com.buttpirate.tbot.bot.configuration.BotConfig;
import com.buttpirate.tbot.bot.service.UpdateHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

@Component
public class CustomBot extends TelegramLongPollingBot {
    @Resource private BotConfig config;
    @Resource private UpdateHandler updateHandler;

    @Override
    public String getBotUsername() {
        return config.botName;
    }

    @Override
    public String getBotToken() {
        return config.token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateHandler.handleUpdate(update);
    }
}
