package com.buttpirate.tbot.bot.DTO;

import com.buttpirate.tbot.bot.model.ChannelModel;
import com.buttpirate.tbot.bot.model.PostModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDTO {
    private PostModel post;
    private ChannelModel channel;
}
