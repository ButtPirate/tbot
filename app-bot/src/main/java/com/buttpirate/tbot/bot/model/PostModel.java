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
public class PostModel extends AbstractModel {
    private long channelId;
    private Integer tgMessageId;
    private Date importDate;
}
