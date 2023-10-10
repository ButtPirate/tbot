package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.CustomBot;
import com.buttpirate.tbot.bot.DTO.CallbackTag;
import com.buttpirate.tbot.bot.DTO.SearchDTO;
import com.buttpirate.tbot.bot.DTO.TagKeyboardBuilder;
import com.buttpirate.tbot.bot.dao.TagDAO;
import com.buttpirate.tbot.bot.model.TagModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.buttpirate.tbot.bot.service.TranslationService.getPhrase;

@Component
public class TagKeyboardService {
    @Resource private CustomBot bot;
    @Resource private TagDAO tagDAO;

    public void sendKeyboard(SearchDTO search) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(search.getTgChatId());
        sendMessage.setText(getPhrase("use-keyboard-below"));

        sendMessage.setReplyMarkup(constructKeyboard(search));

        bot.execute(sendMessage);

    }

    private InlineKeyboardMarkup constructKeyboard(SearchDTO search) {
        List<TagModel> possibleTags = tagDAO.findToDate(search.getStartDate());
        List<CallbackTag> mappedTags = possibleTags.stream().map( (model) -> { return new CallbackTag(model, search.getSelectedTags().contains(model)); } ).collect(Collectors.toList());

        TagKeyboardBuilder builder = new TagKeyboardBuilder(mappedTags, search.getKeyboardPage());

        return builder.build();
    }

    public void updateKeyboard(Update update, SearchDTO search) throws Exception {
        InlineKeyboardMarkup keyboardMarkup = this.constructKeyboard(search);

        EditMessageText updateMessage = new EditMessageText();
        updateMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        updateMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        updateMessage.setText(update.getCallbackQuery().getMessage().getText());
        updateMessage.setReplyMarkup(keyboardMarkup);

        bot.execute(updateMessage);

    }
}
