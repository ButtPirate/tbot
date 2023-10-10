package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.DTO.SearchDTO;
import com.buttpirate.tbot.bot.DTO.TagKeyboardBuilder;
import com.buttpirate.tbot.bot.configuration.BotConfig;
import com.buttpirate.tbot.bot.dao.SearchDAO;
import com.buttpirate.tbot.bot.dao.TagDAO;
import com.buttpirate.tbot.bot.exception.CustomException;
import com.buttpirate.tbot.bot.exception.SessionExpiredException;
import com.buttpirate.tbot.bot.model.SearchModel;
import com.buttpirate.tbot.bot.model.TagModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class SearchSessionService {
    @Resource private SearchDAO searchDAO;
    @Resource private BotConfig config;
    @Resource private TagDAO tagDAO;

    public SearchModel cleanSession(Long tgChatId) {
        searchDAO.purgeSearch(tgChatId);

        SearchModel search = new SearchModel(tgChatId, new Date(), TagKeyboardBuilder.TAGS_ON_PAGE, TagKeyboardBuilder.INITIAL_PAGE_NUMBER, null, null);
        searchDAO.insert(search);

        return search;
    }

    public SearchDTO retrieveSession(Long tgChatId) throws CustomException {
        SearchModel existingSession = searchDAO.find(tgChatId);
        if (existingSession == null) { throw new CustomException("Unable to find session for tgChatId={"+tgChatId+"}", tgChatId); }

        if (this.sessionExpired(existingSession)) { throw new SessionExpiredException(); }
        return this.toDTO(existingSession);
    }

    public boolean sessionExpired(SearchModel session) {
        return ( ( (new Date().getTime() - session.getStartDate().getTime()) / (1000 * 60 ) ) > config.sessionExpirationTime );
    }

    public SearchDTO toDTO(SearchModel model) {
        List<TagModel> selectedTags = tagDAO.find(model);
        SearchDTO dto = new SearchDTO(model);
        dto.setSelectedTags(selectedTags);

        return dto;
    }

}
