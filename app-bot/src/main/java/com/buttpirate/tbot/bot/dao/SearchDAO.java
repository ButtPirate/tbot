package com.buttpirate.tbot.bot.dao;

import com.buttpirate.tbot.bot.DTO.SearchDTO;
import com.buttpirate.tbot.bot.model.SearchModel;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class SearchDAO extends AbstractDAO {
    private static RowMapper<SearchModel> ROW_MAPPER = new BeanPropertyRowMapper<>(SearchModel.class);

    public SearchDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getMainTableName() {
        return "searches";
    }

    @Override
    public RowMapper<SearchModel> getRowMapper() {
        return ROW_MAPPER;
    }

    public void insert(SearchModel model) {
        String query = "" +
                "INSERT INTO searches (\n" +
                "    id, \n" +
                "    tgchatid, \n" +
                "    startdate, \n" +
                "    keyboardpagesize, \n" +
                "    keyboardpage, \n" +
                "    resultpagesize, \n" +
                "    resultpage\n" +
                ") VALUES (\n" +
                "    NEXTVAL('searches_seq'), \n" +
                "    :tgChatId, \n" +
                "    NOW(), \n" +
                "    :keyboardPageSize, \n" +
                "    :keyboardPage, " +
                "    :resultPageSize, \n" +
                "    :resultPage" +
                ")";

        super.insert(query, model);
    }

    public SearchModel find(Long tgChatId) {
        String query = "" +
                "SELECT *\n" +
                "FROM searches \n" +
                "WHERE tgchatid = :tgChatId";
        Map<String, Object> params = map("tgChatId", tgChatId);

        try {
            return jdbcTemplate.queryForObject(query, params, this.getRowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public void addTag(Long searchId, Long tagId) {
        String query = "" +
                "INSERT INTO search_tag_link(\n" +
                "    search_id, \n" +
                "    tag_id\n" +
                ") VALUES (\n" +
                "    :searchId,\n" +
                "    :tagId    \n" +
                ")";
        Map<String, Object> params = map("searchId", searchId, "tagId", tagId);

        jdbcTemplate.update(query, params);

    }

    public void removeTag(Long searchId, Long tagId) {
        String query = "" +
                "DELETE FROM search_tag_link\n" +
                "WHERE search_id = :searchId\n" +
                "AND tag_id = :tagId";

        Map<String, Object> params = map("searchId", searchId, "tagId", tagId);

        jdbcTemplate.update(query, params);

    }

    public void purgeSearch(Long tgChatId) {
        String linkTableQuery = "" +
                "DELETE \n" +
                "FROM search_tag_link\n" +
                "WHERE search_id IN (\n" +
                "    SELECT id\n" +
                "    FROM searches\n" +
                "    WHERE tgchatid = :tgChatId\n" +
                ")";

        String query = "" +
                "DELETE FROM searches\n" +
                "WHERE tgchatid = :tgChatId";

        Map<String, Object> params = map("tgChatId", tgChatId);

        jdbcTemplate.update(linkTableQuery, params);
        jdbcTemplate.update(query, params);

    }

    public void updateKeyboardPage(long searchId, int page) {
        String query = "" +
                "UPDATE searches\n" +
                "SET keyboardpage = :keyboardPage\n" +
                "WHERE id = :id";

        Map<String, Object> params = map("keyboardPage", page, "id", searchId);

        jdbcTemplate.update(query, params);
    }

    public void updateResultPage(SearchDTO search) {
        String query = "" +
                "UPDATE searches\n" +
                "SET resultpage = :resultPage,\n" +
                "    resultPageSize = :resultPageSize\n" +
                "WHERE id = :id";

        Map<String, Object> params = map(
                "resultPage", search.getResultPage(),
                "resultPageSize", search.getResultPageSize(),
                "id", search.getId()

        );

        jdbcTemplate.update(query, params);
    }
}
