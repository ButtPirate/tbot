package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.CustomBot;
import com.buttpirate.tbot.bot.DTO.SearchDTO;
import com.buttpirate.tbot.bot.callbackdata.CallbackData;
import com.buttpirate.tbot.bot.callbackdata.TagButtonCallbackData;
import com.buttpirate.tbot.bot.callbackdata.TagSearchChangePageCallbackData;
import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import com.buttpirate.tbot.bot.dao.SearchDAO;
import com.buttpirate.tbot.bot.dao.TagDAO;
import com.buttpirate.tbot.bot.exception.CustomException;
import com.buttpirate.tbot.bot.exception.SessionExpiredException;
import com.buttpirate.tbot.bot.exception.TooManyTagsSelectedException;
import com.buttpirate.tbot.bot.filter.PostFilter;
import com.buttpirate.tbot.bot.model.SearchModel;
import com.buttpirate.tbot.bot.model.TagModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.Objects;

import static com.buttpirate.tbot.bot.service.TranslationService.getPhrase;

@Component
public class CallbackHandler {
    @Resource private ObjectMapper objectMapper;
    @Resource private PostSearchService postSearchService;
    @Resource private CustomBot bot;
    @Resource private TagKeyboardService tagKeyboardService;
    @Resource private SearchSessionService searchSessionService;
    @Resource private SearchDAO searchDAO;
    @Resource private TagDAO tagDAO;
    @Resource private ChatHandler chatHandler;
    @Resource private ChatService chatService;
          
    public void handleCallbackQuery(Update update) throws Exception {
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (Objects.equals(update.getCallbackQuery().getData(), "null")) {
            return;
        }

        SearchDTO search;
        try {
            search = searchSessionService.retrieveSession(chatId);
        } catch (SessionExpiredException e) {
            DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), update.getCallbackQuery().getMessage().getMessageId());
            bot.execute(deleteMessage);

            chatService.sendMessage(getPhrase("session-expired"), chatId);

            SearchModel newSession = searchSessionService.cleanSession(chatId);
            SearchDTO dto = new SearchDTO(newSession);
            tagKeyboardService.sendKeyboard(dto);
            return;
        }


        CallbackData genericCallbackData = objectMapper.readValue(update.getCallbackQuery().getData(), CallbackData.class);

        switch (genericCallbackData.getType()) {
            case (CallbackDataTypes.POST_SEARCH_CHANGE_PAGE_CALLBACK):
                this.handleChangePage(update, search);
                break;
            case (CallbackDataTypes.POST_SEARCH_TAG_KEYBOARD_CALLBACK):
                this.handleSelectTag(update, search);
                break;
            case (CallbackDataTypes.POST_SEARCH_RUN_SEARCH_CALLBACK):
                this.handleRunSearch(update, search);
                break;
            case (CallbackDataTypes.POST_SEARCH_MORE_RESULTS_CALLBACK):
                this.handleContinueSearch(update, search);
                break;
            case (CallbackDataTypes.POST_SEARCH_SUGGEST_NEW_SEARCH_CALLBACK):
                DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), update.getCallbackQuery().getMessage().getMessageId());
                bot.execute(deleteMessage);

                chatHandler.handleStartCommand(chatId);
                break;
            default:
                throw new CustomException("Unknown callback type", chatId);
        }

    }

    private void handleContinueSearch(Update update, SearchDTO search) throws Exception {
        DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), update.getCallbackQuery().getMessage().getMessageId());
        bot.execute(deleteMessage);

        search.setResultPage(search.getResultPage()+1);
        searchDAO.updateResultPage(search);

        postSearchService.search(search, update.getCallbackQuery().getMessage().getChatId());

    }

    private void handleRunSearch(Update update, SearchDTO search) throws Exception {
        DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), update.getCallbackQuery().getMessage().getMessageId());
        bot.execute(deleteMessage);

        if (search.getSelectedTags().size() == 0) {
            throw new CustomException(getPhrase("no-tags-selected"), update.getCallbackQuery().getMessage().getChatId());
        }

        search.setResultPage(PostFilter.DEFAULT_FIRST_FILTER_PAGE);
        search.setResultPageSize(PostFilter.DEFAULT_RESULT_SIZE);
        searchDAO.updateResultPage(search);

        postSearchService.search(search, update.getCallbackQuery().getMessage().getChatId());

    }

    private void handleSelectTag(Update update, SearchDTO search) throws Exception {
        TagButtonCallbackData callbackData = objectMapper.readValue(update.getCallbackQuery().getData(), TagButtonCallbackData.class);

        TagModel tag = tagDAO.get(callbackData.getTagId());

        if (callbackData.getSelected()) {

            try { search.addTag(tag); } catch (TooManyTagsSelectedException e) {
                DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), update.getCallbackQuery().getMessage().getMessageId());
                bot.execute(deleteMessage);

                throw new CustomException(getPhrase("too-many-tags-selected"), update.getCallbackQuery().getMessage().getChatId());
            }

            searchDAO.addTag(search.getId(), tag.getId());
        } else {
            search.getSelectedTags().remove(tag);
            searchDAO.removeTag(search.getId(), tag.getId());
        }

        tagKeyboardService.updateKeyboard(update, search);
    }

    private void handleChangePage(Update update, SearchDTO search) throws Exception {
        TagSearchChangePageCallbackData callbackData = objectMapper.readValue(update.getCallbackQuery().getData(), TagSearchChangePageCallbackData.class);

        search.setKeyboardPage(callbackData.getKeyboardPage());
        searchDAO.updateKeyboardPage(search.getId(), callbackData.getKeyboardPage());

        tagKeyboardService.updateKeyboard(update, search);
    }

}
