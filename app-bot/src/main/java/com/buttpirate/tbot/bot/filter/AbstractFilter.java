package com.buttpirate.tbot.bot.filter;

import lombok.Data;

@Data
public abstract class AbstractFilter {
    public static int DEFAULT_FIRST_FILTER_PAGE = 1;

    private int pageSize = 20;
    private int page = DEFAULT_FIRST_FILTER_PAGE;

    public String offsetQueryPart() {
        String query = "\n";
        query += "OFFSET " + ((this.page-1)*this.pageSize)+"\n";
        query += "LIMIT " + pageSize+ "\n";

        return query;
    }

    public boolean validate() {
        if (pageSize < 1) { return false; }
        if (pageSize > 1000) { return false; }
        if (page < 1) { return false; }
        if (page > 1000) { return false; }

        return true;
    }

}
