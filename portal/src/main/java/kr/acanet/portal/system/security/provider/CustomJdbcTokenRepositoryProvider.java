package kr.acanet.portal.system.security.provider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

public class CustomJdbcTokenRepositoryProvider extends JdbcDaoSupport implements PersistentTokenRepository {

	private static final Logger logger = LoggerFactory.getLogger(CustomJdbcTokenRepositoryProvider.class);

	public static final String CREATE_TABLE_SQL = "create table if not exists mobile_remember_me (username varchar(64) not null, series varchar(64) primary key, "
			+ "token varchar(64) not null, last_used timestamp not null)";

	public static final String DEF_TOKEN_BY_SERIES_SQL = "select user_id, user_series, user_token, last_login_timestamp from mobile_remember_me where user_series = ?";

	public static final String DEF_INSERT_TOKEN_SQL = "insert into mobile_remember_me (user_id, user_series, user_token, last_login_timestamp) values(?,?,?,?)";

	public static final String DEF_UPDATE_TOKEN_SQL = "update mobile_remember_me set user_token = ?, last_login_timestamp = ? where user_series = ?";

	public static final String DEF_REMOVE_USER_TOKENS_SQL = "delete from mobile_remember_me where user_id = ?";

	private String tokensBySeriesSql = DEF_TOKEN_BY_SERIES_SQL;
	private String insertTokenSql = DEF_INSERT_TOKEN_SQL;
	private String updateTokenSql = DEF_UPDATE_TOKEN_SQL;
	private String removeUserTokensSql = DEF_REMOVE_USER_TOKENS_SQL;
	private boolean createTableOnStartup;

	protected void initDao() {
		if (createTableOnStartup) {
			getJdbcTemplate().execute(CREATE_TABLE_SQL);
		}
	}

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		getJdbcTemplate().update(insertTokenSql, token.getUsername(), token.getSeries(), token.getTokenValue(),
				token.getDate());
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		try {
			return getJdbcTemplate().queryForObject(tokensBySeriesSql, new RowMapper<PersistentRememberMeToken>() {
				public PersistentRememberMeToken mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new PersistentRememberMeToken(rs.getString(1), rs.getString(2), rs.getString(3),
							rs.getTimestamp(4));
				}
			}, seriesId);
		} catch (EmptyResultDataAccessException zeroResults) {
			logger.debug("Querying token for series '" + seriesId + "' returned no results.", zeroResults);
		} catch (IncorrectResultSizeDataAccessException moreThanOne) {
			logger.error("Querying token for series '" + seriesId + "' returned more than one value. Series"
					+ " should be unique");
		} catch (DataAccessException e) {
			logger.error("Failed to load token for series " + seriesId, e);
		}

		return null;
	}

	@Override
	public void removeUserTokens(String username) {
		getJdbcTemplate().update(removeUserTokensSql, username);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		getJdbcTemplate().update(updateTokenSql, tokenValue, lastUsed, series);
	}

	public void setCreateTableOnStartup(boolean createTableOnStartup) {
		this.createTableOnStartup = createTableOnStartup;
	}

}
