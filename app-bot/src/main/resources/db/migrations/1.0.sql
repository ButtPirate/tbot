--liquibase formatted sql
--changeset buttpirate:1.0.sql
--comment Database changes for version 1.0

CREATE TABLE channels (
    id BIGINT PRIMARY KEY,
    tgchatid BIGINT NOT NULL UNIQUE,
    tgtitle VARCHAR
);
CREATE SEQUENCE channels_seq;

CREATE TABLE posts (
    id BIGINT PRIMARY KEY,
    channel_id BIGINT NOT NULL REFERENCES channels(id),
    tgmessageid BIGINT NOT NULL,
    UNIQUE(channel_id, tgmessageid)
);
CREATE SEQUENCE posts_seq;

CREATE TABLE tags (
    id BIGINT PRIMARY KEY,
    text VARCHAR NOT NULL UNIQUE
);
CREATE SEQUENCE tags_seq;

CREATE TABLE post_tag_link (
    post_id BIGINT NOT NULL REFERENCES posts(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    UNIQUE(post_id, tag_id)
);