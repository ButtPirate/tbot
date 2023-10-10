package com.buttpirate.tbot.bot.DTO;

import com.buttpirate.tbot.bot.model.TagModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallbackTag {
    private TagModel tag;
    private boolean selected;

}
