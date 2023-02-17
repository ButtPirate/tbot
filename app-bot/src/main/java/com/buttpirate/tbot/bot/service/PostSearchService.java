package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.CustomBot;
import com.buttpirate.tbot.bot.DTO.PostDTO;
import com.buttpirate.tbot.bot.DTO.PostSearchCallbackData;
import com.buttpirate.tbot.bot.dao.PostDAO;
import com.buttpirate.tbot.bot.dao.TagDAO;
import com.buttpirate.tbot.bot.exception.CustomException;
import com.buttpirate.tbot.bot.filter.PostFilter;
import com.buttpirate.tbot.bot.filter.SearchResult;
import com.buttpirate.tbot.bot.model.TagModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PostSearchService {
    @Resource private TagDAO tagDAO;
    @Resource private CustomBot bot;
    @Resource private PostDAO postDAO;
    @Resource private ObjectMapper objectMapper;

          
    public void searchStart(Message message) throws Exception {
        List<TagModel> retrievedTags;

        retrievedTags = this.validateSearchMessage(message);

        PostFilter filter = new PostFilter(retrievedTags);

        this.search(filter, message.getChatId());

    }

    private void search(PostFilter filter, Long chatId) throws Exception {
        SearchResult<PostDTO> result = postDAO.search(filter);

        // Probably will never execute as you need at least one post to save tag on post import
        if (result.getItems().isEmpty()) {
            throw new CustomException("No posts matching tags found!", chatId);
        }

        for (PostDTO post : result.getItems()) {
            this.forwardPost(post, chatId);
        }

        // "More" message or "No more results" message
        if (filter.getPage() * filter.getPageSize() < result.getPagination().getTotal()) {
            this.sendMoreButton(chatId, result, filter);
        } else {
            this.sendNoMoreResultsMessage(chatId);
        }

    }

    private void sendNoMoreResultsMessage(Long chatId) throws Exception {
        SendMessage sendMessage = new SendMessage(chatId.toString(), "No more results!");
        bot.execute(sendMessage);
    }
          
    private void sendMoreButton(Long chatId, SearchResult<PostDTO> result, PostFilter filter) throws Exception {
        PostSearchCallbackData callbackData = new PostSearchCallbackData(filter);
        String callbackDataString = objectMapper.writeValueAsString(callbackData);

        //TODO!!!
        // Whole callback mechanism needs to be scrapped because of this.
        // Save user searches as DB entity and retrieve data on callback.
        // Only exchange search id on callback.
        if (callbackDataString.getBytes(StandardCharsets.UTF_8).length >=63) {
            this.sendTooManyTagsMessage(chatId);
        }

        SendMessage sendMessage = new SendMessage(chatId.toString(), "Show more?");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton keyboard = new InlineKeyboardButton();

        keyboard.setText(result.getPagination().getTotal()-(filter.getPage()*filter.getPageSize())+" MORE");

        keyboard.setCallbackData(callbackDataString);

        rowInline.add(keyboard);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        bot.execute(sendMessage);
    }

    private void sendTooManyTagsMessage(Long chatId) throws Exception {
        SendMessage sendMessage = new SendMessage(chatId.toString(), "" +
                "Can't show you the rest due to Telegram API restriction" +
                " - too many tags selected. Try searching with less tags. TODO someday!");
        bot.execute(sendMessage);
    }

    private void forwardPost(PostDTO post, Long chatId) throws Exception {
        ForwardMessage forwardMessage = new ForwardMessage(
                chatId.toString(),
                post.getChannel().getTgChatId().toString(),
                post.getPost().getTgMessageId()
        );

        bot.execute(forwardMessage);
    }

    /**
     * Get models for previously saved tags & see if all requested tags are present in DB
     */

    private List<TagModel> validateSearchMessage(Message message) throws Exception {
        List<TagModel> allTags = new ArrayList<>(tagDAO.getAll());
        List<TagModel> presentTags = new ArrayList<>();
        List<String> invalidTags = new ArrayList<>();

        message.getEntities().forEach(
            messageEntity -> {
                TagModel model = allTags.stream().
                        filter( tag -> Objects.equals(messageEntity.getText(), tag.getText()))
                        .findFirst()
                        .orElse(null);

                if (model == null) {
                    invalidTags.add(message.getText());
                    return;
                }

                presentTags.add(model);
            }
        );

        if (!invalidTags.isEmpty()) {
            this.sendAvailableTags(message.getChatId());
            throw new CustomException("Unknown tags: " + String.join(", ", invalidTags), message.getChatId());
        }

        return presentTags;
    }

    public void sendAvailableTags(long chatId) throws Exception {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        List<TagModel> allTags = tagDAO.getAll();

        StringBuilder builder = new StringBuilder();
        builder.append("Available tags:\n");

        allTags.forEach(
            tag -> {
                builder.append(tag.getText());
                builder.append("\n");
            }
        );

        builder.append("Send me one or more tags to see refs");

        message.setText(builder.toString());

        bot.execute(message);
    }

          
    public void continueSearch(PostSearchCallbackData callbackData, Long chatId) throws Exception {
        List<TagModel> tags = tagDAO.getAll().stream().filter(tag -> callbackData.getTagIds().contains(tag.getId())).collect(Collectors.toList());

        PostFilter filter = new PostFilter(tags);
        filter.setPageSize(callbackData.getPageSize());
        filter.setPage(callbackData.getPage()+1);

        this.search(filter, chatId);
    }
}
