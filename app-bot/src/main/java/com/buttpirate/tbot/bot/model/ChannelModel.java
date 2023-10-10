package com.buttpirate.tbot.bot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ChannelModel extends AbstractModel {
    private Long tgChatId;
    private String tgTitle;
    private Date importDate;
}
