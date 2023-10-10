package com.buttpirate.tbot.bot.callbackdata;

import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import com.buttpirate.tbot.bot.model.SearchModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SuggestNewSearchCallbackData extends CallbackData implements Serializable {

    public SuggestNewSearchCallbackData() {
        super.setType(CallbackDataTypes.POST_SEARCH_SUGGEST_NEW_SEARCH_CALLBACK);
    }
}
