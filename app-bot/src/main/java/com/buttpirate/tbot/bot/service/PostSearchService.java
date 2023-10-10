package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.DTO.PostDTO;
import com.buttpirate.tbot.bot.DTO.SearchDTO;
import com.buttpirate.tbot.bot.callbackdata.ContinueSearchCallbackData;
import com.buttpirate.tbot.bot.callbackdata.SuggestNewSearchCallbackData;
import com.buttpirate.tbot.bot.dao.PostDAO;
import com.buttpirate.tbot.bot.exception.CustomException;
import com.buttpirate.tbot.bot.filter.PostFilter;
import com.buttpirate.tbot.bot.filter.SearchResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.buttpirate.tbot.bot.service.TranslationService.getPhrase;

@Component
public class PostSearchService {
    @Resource private PostDAO postDAO;
    @Resource private ChatService chatService;

    public void search(SearchDTO search, long chatId) throws Exception {
        PostFilter filter = new PostFilter(search);

        SearchResult<PostDTO> result = postDAO.search(filter);

        // Probably will never execute as you need at least one post to save tag on post import
        if (result.getItems().isEmpty()) {
            throw new CustomException(getPhrase("no-posts-found"), chatId);
        }

        for (PostDTO post : result.getItems()) {
            chatService.forwardPost(post, chatId);
        }

        // "More" message or "No more results" message
        if (filter.getPage() * filter.getPageSize() < result.getPagination().getTotal()) {
            this.sendMoreButton(chatId, search, result, filter);
        } else {
            this.sendNoMoreResultsMessage(chatId);
        }

    }

    private void sendNoMoreResultsMessage(Long chatId) throws Exception {
        SuggestNewSearchCallbackData callbackData = new SuggestNewSearchCallbackData();

        chatService.sendSimpleButton(getPhrase("no-more-posts"), getPhrase("new-search"), callbackData, chatId);
    }

    private void sendMoreButton(Long chatId, SearchDTO search, SearchResult<PostDTO> result, PostFilter filter) throws Exception {
        ContinueSearchCallbackData callbackData = new ContinueSearchCallbackData(search);

        chatService.sendSimpleButton(getPhrase("show-more"), result.getPagination().getTotal()-(filter.getPage()*filter.getPageSize())+" MORE", callbackData, chatId);
    }

}
