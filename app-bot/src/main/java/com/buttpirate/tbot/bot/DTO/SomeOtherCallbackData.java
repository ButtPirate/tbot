package com.buttpirate.tbot.bot.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SomeOtherCallbackData extends CallbackData {
    private String someField;
}
