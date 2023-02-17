package com.buttpirate.tbot.bot.model.parser;

import lombok.Data;

import java.util.List;

@Data
public class Root {
    private String name;
    private Long id;
    private List<Message> messages;
}
