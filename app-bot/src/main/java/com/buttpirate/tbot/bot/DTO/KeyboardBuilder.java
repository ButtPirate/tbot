package com.buttpirate.tbot.bot.DTO;

import com.buttpirate.tbot.bot.callbackdata.CallbackData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Data
public class KeyboardBuilder {
    private InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    private int maxRowCount;
    private int maxRowSize;
    private ObjectMapper objectMapper = new ObjectMapper();

    public KeyboardBuilder() {
        this(1, 1);
    }

    public KeyboardBuilder(int maxRowCount, int maxRowSize) {
        if (maxRowSize > 8) { throw new InvalidParameterException("Invalid usage"); }

        this.maxRowCount = maxRowCount;
        this.maxRowSize = maxRowSize;

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowsInline.add(rowInline);

        this.markupInline.setKeyboard(rowsInline);
    }

    @SneakyThrows
    public <T extends CallbackData> KeyboardBuilder addButton(String text, T callbackData) {
        List<InlineKeyboardButton> row = this.markupInline.getKeyboard().get(this.markupInline.getKeyboard().size()-1);
        if (row.size() == maxRowSize) {
            if (this.markupInline.getKeyboard().size() == maxRowCount) { throw new InvalidParameterException("Invalid usage"); }

            row = new ArrayList<>();
            this.markupInline.getKeyboard().add(row);
        }

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);

        button.setCallbackData(objectMapper.writeValueAsString(callbackData));

        row.add(button);

        return this;
    }

    public InlineKeyboardMarkup build() {
        return this.markupInline;
    }

}
