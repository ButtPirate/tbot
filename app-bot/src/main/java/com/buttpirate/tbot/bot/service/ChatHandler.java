package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.CustomBot;
import com.buttpirate.tbot.bot.consts.BotCommands;
import com.buttpirate.tbot.bot.exception.CustomException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class ChatHandler {
    @Resource private PostSearchService postSearchService;
    @Resource private CustomBot bot;

    public void handleRequest(Update update) throws Exception {
        Message message = update.getMessage();
        // Handle /commands
        if (message.hasEntities() &&
            message.getEntities().size() == 1 &&
            message.getEntities().stream().allMatch( entity -> entity.getType().equals(EntityType.BOTCOMMAND) )
        ) {
            this.handleSlashCommand(message);
            return;
        }

        if (message.hasEntities() &&
            message.getEntities().stream().anyMatch(entity -> entity.getType().equals(EntityType.HASHTAG))) {
            postSearchService.searchStart(message);
            return;
        }

    }

    private void handleSlashCommand(Message message) throws Exception {
        String command = message.getEntities().get(0).getText();

        switch (command) {
            // Show available tags
            case (BotCommands.START):
                this.sendStartMessage(message.getChatId());
                return;
            default:
                throw new CustomException("Unknown command " + command, message.getChatId());
            // Other commands here
        }
    }

    private void sendStartMessage(long chatId) throws Exception {
        Locale locale = Locale.ENGLISH; // TODO Get user's locale. From Telegram Message API?
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        String messageText = bundle.getString("start-command-response");

        SendMessage message = new SendMessage();
        message.setText(messageText);
        message.setChatId(chatId);
        bot.execute(message);

        postSearchService.sendAvailableTags(chatId);

    }
}
