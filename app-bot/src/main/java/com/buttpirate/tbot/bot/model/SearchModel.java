package com.buttpirate.tbot.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchModel extends AbstractModel {
    private Long tgChatId;
    private Date startDate;
    private int keyboardPageSize;
    private int keyboardPage;
    private Integer resultPageSize;
    private Integer resultPage;
}
