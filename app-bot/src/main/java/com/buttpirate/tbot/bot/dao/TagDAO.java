package com.buttpirate.tbot.bot.dao;

import com.buttpirate.tbot.bot.model.PostModel;
import com.buttpirate.tbot.bot.model.SearchModel;
import com.buttpirate.tbot.bot.model.TagModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class TagDAO extends AbstractDAO {
    private static RowMapper<TagModel> ROW_MAPPER = new BeanPropertyRowMapper<>(TagModel.class);

    public TagDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getMainTableName() {
        return "tags";
    }

    @Override
    public RowMapper<TagModel> getRowMapper() {
        return ROW_MAPPER;
    }

    @CacheEvict(cacheNames = "tags", allEntries = true)
    public void insert(TagModel model) {
        String query = "" +
                "INSERT INTO tags(\n" +
                "    id,\n" +
                "    text\n" +
                ") VALUES(\n" +
                "    NEXTVAL('tags_seq'),\n" +
                "    :text\n" +
                ")";

        super.insert(query, model);
    }

    @Cacheable(cacheNames = "tags")
    public TagModel find(String text) {
        String query = "" +
                "SELECT *\n" +
                "FROM tags \n" +
                "WHERE text = :text";
        Map<String, Object> params = map("text", text);

        try {
            return jdbcTemplate.queryForObject(query, params, this.getRowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public void link(PostModel post, TagModel tag) {
        String query = "" +
                "INSERT INTO post_tag_link(\n" +
                "    post_id,\n" +
                "    tag_id\n" +
                ") VALUES (\n" +
                "    :postId,\n" +
                "    :tagId\n" +
                ")";

        Map<String, Object> params = map("postId", post.getId(), "tagId", tag.getId());

        jdbcTemplate.update(query, params);

    }

    @Cacheable(cacheNames = "tags")
    public List<TagModel> getAll() {
        return super.getAll();
    }

    @Cacheable(cacheNames = "tags")
    public TagModel get(long id) {
        return super.get(id);
    }

    public List<TagModel> findToDate(Date date) {
        String query = "" +
                "SELECT *\n" +
                "FROM tags\n" +
                "WHERE import_date <= :importDate";

        Map<String, Object> params = map("importDate", date);

        return jdbcTemplate.query(query, params, this.getRowMapper());
    }

    public List<TagModel> find(SearchModel search) {
        String query = "" +
                "SELECT tags.*\n" +
                "FROM tags\n" +
                "JOIN search_tag_link ON tags.id = search_tag_link.tag_id\n" +
                "WHERE search_tag_link.search_id = :searchId";

        Map<String, Object> params = map("searchId", search.getId());

        return jdbcTemplate.query(query, params, this.getRowMapper());

    }

}
