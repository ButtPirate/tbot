package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.CustomBot;
import com.buttpirate.tbot.bot.DTO.CallbackData;
import com.buttpirate.tbot.bot.DTO.PostSearchCallbackData;
import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

@Component
public class CallbackHandler {
    @Resource private ObjectMapper objectMapper;
    @Resource private PostSearchService postSearchService;
    @Resource private CustomBot bot;
          
    public void handleCallbackQuery(Update update) throws Exception {
        CallbackData genericCallbackData = objectMapper.readValue(update.getCallbackQuery().getData(), CallbackData.class);

        switch (genericCallbackData.getType()) {
            default:
            case (CallbackDataTypes.POST_SEARCH_CALLBACK):
                this.deleteCallbackButton(update);

                PostSearchCallbackData callbackData = objectMapper.readValue(update.getCallbackQuery().getData(), PostSearchCallbackData.class);

                postSearchService.continueSearch(callbackData, update.getCallbackQuery().getMessage().getChatId());

                break;
            // Other callback types here.
        }

    }
          
    private void deleteCallbackButton(Update update) throws Exception {
        DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), update.getCallbackQuery().getMessage().getMessageId());
        bot.execute(deleteMessage);
    }

}
