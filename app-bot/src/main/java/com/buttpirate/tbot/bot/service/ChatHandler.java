package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.DTO.SearchDTO;
import com.buttpirate.tbot.bot.consts.BotCommands;
import com.buttpirate.tbot.bot.exception.CustomException;
import com.buttpirate.tbot.bot.model.SearchModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

import static com.buttpirate.tbot.bot.service.TranslationService.getPhrase;

@Component
public class ChatHandler {
    @Resource private SearchSessionService searchSessionService;
    @Resource private TagKeyboardService tagKeyboardService;
    @Resource private ChatService chatService;

    public void handleMessage(Update update) throws Exception {
        Message message = update.getMessage();
        // Handle /commands
        if (message.hasEntities() &&
            message.getEntities().size() == 1 &&
            message.getEntities().stream().allMatch( entity -> entity.getType().equals(EntityType.BOTCOMMAND) )
        ) {
            this.handleSlashCommand(message);
            return;
        }

        throw new CustomException("Unknown command. Use /start.", message.getChatId());

    }

    private void handleSlashCommand(Message message) throws Exception {
        String command = message.getEntities().get(0).getText();

        switch (command) {
            // Search session start
            case (BotCommands.START):
                this.handleStartCommand(message.getChatId());
                return;
            default:
                throw new CustomException("Unknown command " + command+". Use /start.", message.getChatId());
            // Other commands here
        }
    }

    public void handleStartCommand(Long chatId) throws Exception {
        sendStartMessage(chatId);

        SearchModel session = searchSessionService.cleanSession(chatId);
        SearchDTO dto = new SearchDTO(session);

        tagKeyboardService.sendKeyboard(dto);

    }

    private void sendStartMessage(long chatId) throws Exception {
        chatService.sendMessage(getPhrase("start-command-response"), chatId);
    }

}
