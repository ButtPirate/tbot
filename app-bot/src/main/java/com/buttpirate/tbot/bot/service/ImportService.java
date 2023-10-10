package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.dao.ChannelDAO;
import com.buttpirate.tbot.bot.dao.PostDAO;
import com.buttpirate.tbot.bot.dao.TagDAO;
import com.buttpirate.tbot.bot.model.ChannelModel;
import com.buttpirate.tbot.bot.model.PostModel;
import com.buttpirate.tbot.bot.model.TagModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class ImportService {
    @Resource private ChannelDAO channelDAO;
    @Resource private TagDAO tagDAO;
    @Resource private PostDAO postDAO;

    public void handleImport(Update update) {
        ChannelModel channel = this.fetchChannel(update.getChannelPost().getChat());

        // Only if tags are present
        if (update.getChannelPost().getEntities() != null &&
            update.getChannelPost().getEntities().stream().anyMatch( entity -> entity.getType().equals(EntityType.HASHTAG) )) {
            PostModel post = new PostModel(channel.getId(), update.getChannelPost().getMessageId(), new Date());
            postDAO.insert(post);

            update.getChannelPost().getEntities().stream()
                .filter( entity -> entity.getType().equals(EntityType.HASHTAG))
                .forEach(
                    (entity) -> {
                        TagModel tag = this.fetchTag(entity.getText());
                        tagDAO.link(post, tag);
                    }
                );
        }
    }

    private ChannelModel fetchChannel(Chat chat) {
        ChannelModel channel = channelDAO.find(chat.getId());

        if (channel == null) {
            channel = new ChannelModel();
            channel.setTgChatId(chat.getId());
            channel.setTgTitle(chat.getTitle());
            channelDAO.insert(channel);
        }

        return channel;
    }

    private TagModel fetchTag(String tagText) {
        TagModel tag = tagDAO.find(tagText);

        if (tag == null) {
            tag = new TagModel(tagText, new Date());
            tagDAO.insert(tag);
        }

        return tag;
    }

}
