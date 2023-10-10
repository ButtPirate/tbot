package com.buttpirate.tbot.bot.DTO;

import com.buttpirate.tbot.bot.callbackdata.RunSearchCallbackData;
import com.buttpirate.tbot.bot.callbackdata.TagButtonCallbackData;
import com.buttpirate.tbot.bot.callbackdata.TagSearchChangePageCallbackData;
import lombok.Data;

import java.util.List;

import static com.buttpirate.tbot.bot.service.TranslationService.getPhrase;

@Data
public class TagKeyboardBuilder extends KeyboardBuilder {
    private static final int TAG_ROW_COUNT = 3;
    private static final int TAG_COL_COUNT = 3;
    public static final int TAGS_ON_PAGE = TAG_COL_COUNT * TAG_ROW_COUNT;
    public static final int INITIAL_PAGE_NUMBER = 0; // Tired of searching whether it is 0 or 1
    public static final String CHECKMARK = "\u2705";

    public TagKeyboardBuilder(List<CallbackTag> tags, int page) {
        super(TAG_ROW_COUNT+2, TAG_COL_COUNT);

        int startIndex = TAGS_ON_PAGE * page;
        int endIndex = Math.min(startIndex + TAGS_ON_PAGE, tags.size());
        List<CallbackTag> tagPage = tags.subList( startIndex, endIndex);

        tagPage.forEach(
            (tag) -> {
                String buttonText = tag.isSelected() ? tag.getTag().getText() + " "+CHECKMARK+"" : tag.getTag().getText();
                super.addButton(buttonText, new TagButtonCallbackData(tag.getTag().getId(), !tag.isSelected(), page));
            }
        );

        int tagsInLastRow = tagPage.size();
        while (tagsInLastRow % 3 != 0) {
            super.addButton(" ", null);
            tagsInLastRow++;
        }

        if (startIndex == INITIAL_PAGE_NUMBER) {
            super.addButton(getPhrase("prev-button"), null);
        } else {
            super.addButton(getPhrase("prev-button"), new TagSearchChangePageCallbackData(page-1));
        }

        super.addButton(getPhrase("run-search"), new RunSearchCallbackData());

        if (endIndex != tags.size()) {
            super.addButton(getPhrase("next-button"), new TagSearchChangePageCallbackData(page+1));
        } else {
            super.addButton(getPhrase("next-button"), null);
        }



    }
}
