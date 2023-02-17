package com.buttpirate.tbot.bot.service;

import com.buttpirate.tbot.bot.configuration.ParserConfig;
import com.buttpirate.tbot.bot.dao.ChannelDAO;
import com.buttpirate.tbot.bot.dao.PostDAO;
import com.buttpirate.tbot.bot.dao.TagDAO;
import com.buttpirate.tbot.bot.model.ChannelModel;
import com.buttpirate.tbot.bot.model.PostModel;
import com.buttpirate.tbot.bot.model.TagModel;
import com.buttpirate.tbot.bot.model.parser.Message;
import com.buttpirate.tbot.bot.model.parser.Root;
import com.buttpirate.tbot.bot.model.parser.TextEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SuppressWarnings("SpringDependsOnUnresolvedBeanInspection")
@Component
@DependsOn("liquibase")
public class ParserService {
    @Resource private ParserConfig parserConfig;
    @Resource private ChannelDAO channelDAO;
    @Resource private ObjectMapper objectMapper;
    @Resource private TagDAO tagDAO;
    @Resource private PostDAO postDAO;

    @PostConstruct
    public void runImport() {
        if (parserConfig.filepaths == null || parserConfig.filepaths.length == 0) {
            log.info("No import file specified, skipping...");
            return;
        }

        for (String filepath : parserConfig.filepaths) {
            log.info("Found configured import file <"+filepath+">, parsing...");
            try {
                this.parseData(filepath);
            } catch (Exception e) {
                log.error("Error on parsing import data, skipping...", e);
            }
        }


    }

    private void parseData(String path) throws IOException {
        File file = new File(path);
        String json = FileUtils.readFileToString(file, "UTF-8");

        Root root = objectMapper.readValue(json, Root.class);
        Map<TextEntity, List<Message>> map = new HashMap<>();
        root.getMessages().forEach(
            (message) -> {
                message.getEntities().forEach(
                    (entity) -> {
                        if (entity.getType().equals("hashtag")) {
                            List<Message> list = map.computeIfAbsent(entity, k -> new ArrayList<>());
                            list.add(message);
                        }
                    }
                );
            }
        );

        // TODO this is dumb but works on small sample size of 2 channels. check API docs
        Long fixedChannelId = Long.parseLong("-100"+root.getId().toString());

        ChannelModel channel = new ChannelModel();
        channel.setTgChatId(fixedChannelId);
        channel.setTgTitle(root.getName());
        try { channelDAO.insert(channel); } catch (DuplicateKeyException e) { channel = channelDAO.find(fixedChannelId); log.info("Channel <"+channel+"> already saved..."); }
        ChannelModel finalChannel = channel;

        map.forEach((key, value) -> {
            TagModel tag = new TagModel();
            tag.setText(key.getText());
            try {
                tagDAO.insert(tag);
            } catch (DuplicateKeyException e) {
                tag = tagDAO.find(tag.getText());
                log.info("Tag <" + tag + "> already saved...");
            }
            TagModel finalTag = tag;

            value.forEach(
                (message) -> {
                    PostModel post = new PostModel();
                    post.setChannelId(finalChannel.getId());
                    post.setTgMessageId(message.getId());

                    try {
                        postDAO.insert(post);
                    } catch (DuplicateKeyException e) {
                        post = postDAO.find(message.getId(), finalChannel.getId());
                        log.info("Post <" + post + "> already saved...");
                    }

                    try {
                        tagDAO.link(post, finalTag);
                    } catch (DuplicateKeyException e) {
                        log.info("Tag <" + finalTag + "> and post <" + post + "> already linked...");
                    }
                }
            );
        });

        log.info("Import done!");

    }

}
