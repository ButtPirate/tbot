package com.buttpirate.tbot.bot.callbackdata;

import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagButtonCallbackData extends CallbackData {
    private Long tagId;
    private Boolean selected;
    private int page;

    public TagButtonCallbackData(Long tagId, Boolean selected, int page) {
        super.setType(CallbackDataTypes.POST_SEARCH_TAG_KEYBOARD_CALLBACK);
        this.tagId = tagId;
        this.selected = selected;
        this.page = page;
    }
}
