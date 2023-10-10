package com.buttpirate.tbot.bot.DTO;

import com.buttpirate.tbot.bot.exception.TooManyTagsSelectedException;
import com.buttpirate.tbot.bot.model.SearchModel;
import com.buttpirate.tbot.bot.model.TagModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO extends SearchModel {
    private static int MAX_TAGS_ON_SEARCH = 15;

    private List<TagModel> selectedTags;

    public SearchDTO(SearchModel model) {
        super.setId(model.getId());
        super.setTgChatId(model.getTgChatId());
        super.setStartDate(model.getStartDate());
        super.setKeyboardPage(model.getKeyboardPage());
        super.setKeyboardPageSize(model.getKeyboardPageSize());
        super.setResultPage(model.getResultPage());
        super.setResultPageSize(model.getResultPageSize());

        this.selectedTags = Collections.emptyList();
    }

    public void addTag(TagModel tag) {
        if (this.getSelectedTags().size() == MAX_TAGS_ON_SEARCH) { throw new TooManyTagsSelectedException(); }

        this.selectedTags.add(tag);
    }
}
