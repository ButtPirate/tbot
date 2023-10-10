package com.buttpirate.tbot.bot.callbackdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CallbackData implements Serializable {
    @JsonProperty("t") //CallbackData max size is 64bytes...
    private String type;
}
