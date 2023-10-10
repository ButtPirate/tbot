package com.buttpirate.tbot.bot.filter;

import com.buttpirate.tbot.bot.DTO.SearchDTO;
import com.buttpirate.tbot.bot.model.TagModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostFilter extends AbstractFilter {
    public static int DEFAULT_RESULT_SIZE = 2;

    private List<TagModel> tags;
    private Date startDate;

    public PostFilter(SearchDTO search) {
        this.tags = search.getSelectedTags();
        this.startDate = search.getStartDate();
        this.setPageSize(search.getResultPageSize());
        this.setPage(search.getResultPage());
    }

}
