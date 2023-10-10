package com.buttpirate.tbot.bot.dao;

import com.buttpirate.tbot.bot.configuration.CustomBeanPropertySqlParameterSource;
import com.buttpirate.tbot.bot.filter.AbstractFilter;
import com.buttpirate.tbot.bot.filter.Pagination;
import com.buttpirate.tbot.bot.filter.SearchResult;
import com.buttpirate.tbot.bot.model.AbstractModel;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDAO {
	protected final NamedParameterJdbcTemplate jdbcTemplate;

    public AbstractDAO(DataSource dataSource) {
    	this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    	JdbcOperations operations = jdbcTemplate.getJdbcOperations();
    	if (operations instanceof JdbcTemplate) {
    		((JdbcTemplate) operations).setFetchSize(1000);
    	}
    }

    public abstract String getMainTableName();

    public abstract <T extends AbstractModel> RowMapper<T> getRowMapper();

    public <T extends AbstractModel> T get(long id) {
        String query = "" +
                "SELECT *\n" +
                "FROM "+this.getMainTableName()+"\n" +
                "WHERE id = :id";

        Map<String, Object> params = map(
                "id", id
        );

        return selectOne(query, params, this.getRowMapper());
    }

    public <T extends AbstractModel> void insert(String query, T model) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource params = new CustomBeanPropertySqlParameterSource(model);

        jdbcTemplate.update(query, params, keyHolder, new String[]{"id"});

        model.setId(keyHolder.getKey().longValue());
    }

	protected static Map<String, Object> map(Object... keysAndValues) {
        Map<String, Object> result = new HashMap<>();
        for (int i=0; i<keysAndValues.length; i+=2) {
            result.put((String) keysAndValues[i], keysAndValues[i+1]);
        }
        return result;
    }

    protected <T> T selectOne(String sql, Map<String, Object> params, RowMapper<T> mapper) {
        List<T> items = jdbcTemplate.query(sql,params, mapper);
        return items.isEmpty() ? null : items.get(0);
    }

    protected <T> T selectOne(String sql, Map<String, Object> params, ResultSetExtractor<List<T>> extractor) {
        List<T> items = jdbcTemplate.query(sql, params, extractor);
        return items.isEmpty() ? null : items.get(0);
    }

    protected void update(String query, AbstractModel model) {
        SqlParameterSource params = new CustomBeanPropertySqlParameterSource(model);

        jdbcTemplate.update(query, params);
    }

    public boolean exists(long id) {
        String query = "" +
                "SELECT EXISTS(\n" +
                "    SELECT id \n" +
                "    FROM "+this.getMainTableName()+" \n" +
                "    WHERE id = :id\n" +
                ")";

        Map<String, Object> params = map(
                "id", id
        );

        return jdbcTemplate.queryForObject(query, params, boolean.class);
    }

    public void delete(long id) {
        String query = "" +
                "DELETE FROM "+this.getMainTableName()+"\n" +
                "WHERE id = :id";

        Map<String, Object> params = map(
                "id", id
        );

        jdbcTemplate.update(query, params);

    }

    public <T extends AbstractModel, V extends AbstractFilter> SearchResult<T> search(V filter, String customizedQueryPart, Map<String, Object> params) {
        String query = "" +
                "FROM "+this.getMainTableName()+"\n" +
                "WHERE 1=1\n" +
                customizedQueryPart;

        String mainQuery = "" +
                "SELECT *\n" +
                query +
                filter.offsetQueryPart();

        String countQuery = "" +
                "SELECT COUNT(*)\n" +
                query;

        return this.search(filter, params, mainQuery, countQuery);
    }

    public <T extends AbstractModel, V extends AbstractFilter> SearchResult<T> search(V filter, Map<String, Object> params, String mainQuery, String countQuery) {
        List<T> items = jdbcTemplate.query(mainQuery, params, this.getRowMapper());
        if (items.isEmpty()) { items = Collections.emptyList(); }

        Integer total = jdbcTemplate.queryForObject(countQuery, params, Integer.class);

        Pagination pag = new Pagination(filter.getPage(), total);
        return new SearchResult<T>(items, pag);

    }

    public static String sqlContainsText(String modelFieldName, String columnFieldName) {
        return " (POSITION(LOWER(:"+modelFieldName+") IN LOWER("+columnFieldName+"))) > 0 ";
    }

    public List<String> fieldTooltip(String fieldName, String query) {
        fieldName = camelToSnake(fieldName);

        String sql = "" +
                "SELECT DISTINCT "+fieldName+"\n" +
                "FROM "+getMainTableName()+"\n";

        if (query != null && !query.isBlank()) {
            sql += "WHERE "+sqlContainsText(fieldName, fieldName) + "\n";
        }

        sql += "LIMIT 10";

        Map<String, Object> params = map(fieldName, query);

        return jdbcTemplate.queryForList(sql, params, String.class);

    }

    public String camelToSnake(String camel) {
        String underscore;
        underscore = String.valueOf(Character.toLowerCase(camel.charAt(0)));
        for (int i = 1; i < camel.length(); i++) {
            underscore += Character.isLowerCase(camel.charAt(i)) ? String.valueOf(camel.charAt(i))
                    : "_" + String.valueOf(Character.toLowerCase(camel.charAt(i)));
        }
        return underscore;

    }

    public <T extends AbstractModel> List<T> getAll() {
        String query = "" +
                "SELECT * \n" +
                "FROM "+getMainTableName()+"";

        return jdbcTemplate.query(query, Collections.emptyMap(), this.getRowMapper());

    }

}
