package com.buttpirate.tbot.bot.callbackdata;

import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagSearchChangePageCallbackData extends CallbackData {
    @JsonProperty("keyboardPage")
    private int keyboardPage;

    public TagSearchChangePageCallbackData(int page) {
        super.setType(CallbackDataTypes.POST_SEARCH_CHANGE_PAGE_CALLBACK);
        this.keyboardPage = page;
    }
}
