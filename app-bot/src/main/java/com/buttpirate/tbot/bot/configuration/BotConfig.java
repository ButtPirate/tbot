package com.buttpirate.tbot.bot.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource({"classpath:application.properties"})
public class BotConfig {
    @Value("${bot.name}")
    public String botName;

    @Value("${bot.token}")
    public String token;

    @Value("${bot.session.expire.min}")
    public Integer sessionExpirationTime;

    @Value("${bot.debug.enabled}")
    public boolean debugEnabled;

    // You can figure out user's locale using Telegram API (message.from.languageCode), but it gets messy.
    // You can't store it in Search object, you need a real user session that lives in-memory, otherwise you
    // need to pass it everywhere, even in exceptions. Not worth it right now. Configure in application.properties.
    public static String selectedLocale;

    @Value("${bot.selected-locale}")
    public void setSelectedLocale(String selectedLocale) {
        BotConfig.selectedLocale = selectedLocale;
    }

}
