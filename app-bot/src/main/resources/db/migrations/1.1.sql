--liquibase formatted sql
--changeset buttpirate:1.1.sql
--comment Database changes for version 1.1.

ALTER TABLE tags ADD COLUMN IF NOT EXISTS import_date TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE posts ADD COLUMN IF NOT EXISTS import_date TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE channels ADD COLUMN IF NOT EXISTS import_date TIMESTAMP NOT NULL DEFAULT NOW();

CREATE TABLE searches (
    id BIGINT PRIMARY KEY,
    tgchatid BIGINT NOT NULL UNIQUE,
    startdate TIMESTAMP NOT NULL,
    keyboardpagesize INT NOT NULL,
    keyboardpage INT NOT NULL,
    resultpagesize INT,
    resultpage INT
);
CREATE SEQUENCE searches_seq;

CREATE TABLE search_tag_link (
    search_id BIGINT NOT NULL REFERENCES searches(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id)
)