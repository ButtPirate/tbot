package com.buttpirate.tbot.bot.callbackdata;

import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
//@AllArgsConstructor
public class RunSearchCallbackData extends CallbackData {
    public RunSearchCallbackData() {
        super.setType(CallbackDataTypes.POST_SEARCH_RUN_SEARCH_CALLBACK);
    }
}
