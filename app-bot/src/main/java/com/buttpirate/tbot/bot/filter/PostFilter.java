package com.buttpirate.tbot.bot.filter;

import com.buttpirate.tbot.bot.model.TagModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostFilter extends AbstractFilter {
    private List<TagModel> tags;

    public PostFilter(List<TagModel> tags) {
        this.tags = tags;
        this.setPageSize(2);
    }
}
