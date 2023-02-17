package com.buttpirate.tbot.bot.dao;

import com.buttpirate.tbot.bot.DTO.PostDTO;
import com.buttpirate.tbot.bot.filter.Pagination;
import com.buttpirate.tbot.bot.filter.PostFilter;
import com.buttpirate.tbot.bot.filter.SearchResult;
import com.buttpirate.tbot.bot.model.AbstractModel;
import com.buttpirate.tbot.bot.model.ChannelModel;
import com.buttpirate.tbot.bot.model.PostModel;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class PostDAO extends AbstractDAO {
    private static RowMapper<PostModel> POST_ROW_MAPPER = new BeanPropertyRowMapper<>(PostModel.class);
    private static RowMapper<ChannelModel> CHANNEL_ROW_MAPPER = new BeanPropertyRowMapper<>(ChannelModel.class);
    private static ResultSetExtractor<List<PostDTO>> POST_DTO_EXTRACTOR = new PostDTOExtractor();

    private static class PostDTOExtractor implements ResultSetExtractor<List<PostDTO>> {
        @Override
        public List<PostDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<PostDTO> result = new ArrayList<>();

            while (rs.next()) {
                PostModel post = POST_ROW_MAPPER.mapRow(rs, 0);
                ChannelModel channel = CHANNEL_ROW_MAPPER.mapRow(rs, 0);

                result.add(new PostDTO(post, channel));
            }

            return result;
        }
    }

    public PostDAO(DataSource dataSource) { super(dataSource); }

    @Override
    public String getMainTableName() { return "posts"; }

    @Override
    public RowMapper<PostModel> getRowMapper() {
        return POST_ROW_MAPPER;
    }

    public void insert(PostModel model) {
        String query = "" +
                "INSERT INTO posts(\n" +
                "    id, \n" +
                "    channel_id,\n" +
                "    tgmessageid\n" +
                ") VALUES (\n" +
                "    NEXTVAL('posts_seq'),\n" +
                "    :channelId,\n" +
                "    :tgMessageId\n" +
                ")";

        super.insert(query, model);
    }

    public SearchResult<PostDTO> search(PostFilter filter) {
        String mainQuery = "" +
                "WITH inner_posts AS (\n" +
                "    SELECT link.post_id, COUNT(link.post_id) AS relevancy\n" +
                "    FROM posts\n" +
                "             JOIN post_tag_link link on posts.id = link.post_id\n" +
                "    WHERE link.tag_id IN (:tagIds)\n" +
                "    GROUP BY link.post_id\n" +
                "    ORDER BY relevancy DESC\n" +
                ")\n" +
                "SELECT outer_posts.*, channels.*\n" +
                "FROM posts outer_posts\n" +
                "JOIN channels on outer_posts.channel_id = channels.id\n" +
                "JOIN inner_posts ON inner_posts.post_id = outer_posts.id\n" +
                "ORDER BY inner_posts.relevancy DESC\n" +
                filter.offsetQueryPart();

        String countQuery = "" +
                "SELECT COUNT(DISTINCT post_id)\n" +
                "FROM posts\n" +
                "JOIN post_tag_link link on posts.id = link.post_id\n" +
                "WHERE link.tag_id IN (:tagIds)";

        List<Long> tagIds = filter.getTags().stream()
                .map(AbstractModel::getId)
                .collect(Collectors.toList());
        Map<String, Object> params = map("tagIds", tagIds);

        List<PostDTO> items = jdbcTemplate.query(mainQuery, params, POST_DTO_EXTRACTOR);

        if (items == null || items.isEmpty()) { items = Collections.emptyList(); }

        Integer total = jdbcTemplate.queryForObject(countQuery, params, Integer.class);
        if (total == null) {total = 0;}

        Pagination pag = new Pagination(filter.getPage(), total);
        return new SearchResult<PostDTO>(items, pag);
    }

    public PostModel find(long tgMessageId, long channelId) {
        String query = "" +
                "SELECT *\n" +
                "FROM posts\n" +
                "WHERE tgmessageid = :tgMessageId\n" +
                "AND channel_id = :channelId";

        Map<String, Object> params = map("tgMessageId", tgMessageId, "channelId", channelId);

        try {
            return jdbcTemplate.queryForObject(query, params, this.getRowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

}
