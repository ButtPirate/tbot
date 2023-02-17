package com.buttpirate.tbot.bot.dao;

import com.buttpirate.tbot.bot.model.ChannelModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class ChannelDAO extends AbstractDAO {
    private static RowMapper<ChannelModel> ROW_MAPPER = new BeanPropertyRowMapper<>(ChannelModel.class);

    public ChannelDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getMainTableName() {
        return "channels";
    }

    @Override
    public RowMapper<ChannelModel> getRowMapper() {
        return ROW_MAPPER;
    }

    @CacheEvict(cacheNames = "channels", allEntries = true)
    public void insert(ChannelModel model) {
        String query = "" +
                "INSERT INTO channels(\n" +
                "    id,\n" +
                "    tgchatid,\n" +
                "    tgtitle\n" +
                ") VALUES(\n" +
                "    NEXTVAL('channels_seq'),\n" +
                "    :tgChatId,\n" +
                "    :tgTitle\n" +
                ")";

        super.insert(query, model);
    }

    @Cacheable(cacheNames = "channels")
    public ChannelModel find(Long tgChatId) {
        String query = "" +
                "SELECT *\n" +
                "FROM channels \n" +
                "WHERE tgchatid = :tgChatId";
        Map<String, Object> params = map("tgChatId", tgChatId);

        try {
            return jdbcTemplate.queryForObject(query, params, this.getRowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
}
