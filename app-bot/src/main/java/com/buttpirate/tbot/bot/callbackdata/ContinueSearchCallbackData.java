package com.buttpirate.tbot.bot.callbackdata;

import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import com.buttpirate.tbot.bot.filter.PostFilter;
import com.buttpirate.tbot.bot.model.AbstractModel;
import com.buttpirate.tbot.bot.model.SearchModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContinueSearchCallbackData extends CallbackData implements Serializable {
    private Long searchId;

    public ContinueSearchCallbackData(SearchModel search) {
        super.setType(CallbackDataTypes.POST_SEARCH_MORE_RESULTS_CALLBACK);
        this.searchId = search.getId();
    }
}
