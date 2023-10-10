package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.configuration.BotConfig;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationService {
    public static String getPhrase(String key) {
        Locale locale = new Locale(BotConfig.selectedLocale);
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }
}
