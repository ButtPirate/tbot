package com.buttpirate.tbot.bot.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResult<T> {
    private List<T> items;
    private Pagination pagination;
}
