package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.CustomBot;
import com.buttpirate.tbot.bot.DTO.PostDTO;
import com.buttpirate.tbot.bot.callbackdata.CallbackData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChatService {
    @Resource private CustomBot bot;
    @Resource private ObjectMapper objectMapper;

    public void sendMessage(String messageText, long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setText(messageText);
        message.setChatId(chatId);
        bot.execute(message);
    }

    public void sendSimpleButton(String messageText, String buttonText, CallbackData callbackData, Long chatId) throws Exception {
        String callbackDataString = objectMapper.writeValueAsString(callbackData);

        SendMessage sendMessage = new SendMessage(chatId.toString(), messageText);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton keyboard = new InlineKeyboardButton();

        keyboard.setText(buttonText);

        keyboard.setCallbackData(callbackDataString);

        rowInline.add(keyboard);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        bot.execute(sendMessage);

    }

    public void forwardPost(PostDTO post, Long chatId) throws Exception {
        ForwardMessage forwardMessage = new ForwardMessage(
                chatId.toString(),
                post.getChannel().getTgChatId().toString(),
                post.getPost().getTgMessageId()
        );

        bot.execute(forwardMessage);
    }
}
