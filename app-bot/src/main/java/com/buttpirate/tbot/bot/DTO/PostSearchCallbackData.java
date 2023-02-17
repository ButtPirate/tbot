package com.buttpirate.tbot.bot.DTO;

import com.buttpirate.tbot.bot.consts.CallbackDataTypes;
import com.buttpirate.tbot.bot.filter.PostFilter;
import com.buttpirate.tbot.bot.model.AbstractModel;
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
public class PostSearchCallbackData extends CallbackData implements Serializable {
    @JsonProperty("ti")
    private List<Long> tagIds;
    @JsonProperty("ps")
    private int pageSize;
    @JsonProperty("p")
    private int page;

    public PostSearchCallbackData(PostFilter filter) {
        super.setType(CallbackDataTypes.POST_SEARCH_CALLBACK);
        this.setTagIds(filter.getTags().stream().map(AbstractModel::getId).collect(Collectors.toList()));
        this.pageSize = filter.getPageSize();
        this.page = filter.getPage();
    }
}
