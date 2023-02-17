package com.buttpirate.tbot.bot.filter;

import lombok.Data;

@Data
public abstract class AbstractFilter {
    private int pageSize = 20;
    private int page = 1;

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
