package net.sokontokoro_factory.api.game.score;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sokontokoro_factory.api.util.Property;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScoreService {


	private static Connection getConnection() {

        Connection connection = null;
		try {
	        Class.forName(Property.DB_DRIVER());
	        connection = DriverManager.getConnection(Property.DB_URL(), Property.DB_USER(), Property.DB_PASSWORD());
			return connection;
		} catch (ConfigurationException e){
		} catch (ClassNotFoundException e){
		} catch (SQLException e){
		}
		return connection;
	}
	
	
	public static void insertScore(
								String game_name, 
								int user_id, 
								int point)
								throws SQLException {

		String sql = "INSERT INTO score"
				+ " (game_name, user_id, point, create_date,update_date,final_date,count)"
				+ " VALUES (?,?,?,NOW(),NOW(),NOW(),1)"	// 初回登録
				+ " ON DUPLICATE KEY UPDATE"				// ↓2回目以降
				+ " update_date = IF(VALUES(point) > point, values(update_date), update_date),"
				+ " point = IF(VALUES(point) > point, VALUES(point), point),"
				+ " final_date = NOW(),"
				+ " count = count + 1";
				
		PreparedStatement statement = null;
		Connection connection = getConnection();

		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			statement.setInt(2, user_id);
			statement.setInt(3, point);
			statement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			try{
				if (connection != null) {
					connection.close();
				}
				if (statement != null) {
					statement.close();
				}
			}catch(SQLException e){
				throw e;
			}
		}
	}

	public static JSONArray getScores(
								String game_name)
								throws Exception{

		String sql = "select * from score where game_name = ?";

		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			rs = statement.executeQuery();
			while (rs.next()) {
				JSONObject score = new JSONObject();
				score.put("point", rs.getInt("point"));
				scores.put(score);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		return scores;
	}
	public static JSONObject getTotalNumber(
			String game_name)
			throws Exception{

		String sql = "select count(*) from score where game_name = ?";

		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONObject result = new JSONObject();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			rs = statement.executeQuery();
			rs.next();
			result.put("count", rs.getInt("count(*)"));
		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}
	public static JSONObject getMyInfo(
			String game_name, 
			int user_id)
			throws Exception{

		String sql_ranking = "select count(*)+1 as ranking  from score"
				+ " where point > (select point from score where user_id = ? and game_name = ?)"
				+ " and game_name = ?";
		
		
		String sql_userInfo = " select * , user.name as user_name from score"
				+ " left join user"
				+ " on score.user_id = user.id"
				+ " where user_id = ?"
				+ " and game_name = ?";
		
		PreparedStatement statement_ranking = null;
		PreparedStatement statement_userInfo = null;
		Connection connection_ranking = getConnection();
		Connection connection_userInfo = getConnection();
		ResultSet rs = null;
		JSONObject info = new JSONObject();

		try {
			statement_ranking = connection_ranking.prepareStatement(sql_ranking);
			statement_ranking.setInt(1, user_id);
			statement_ranking.setString(2, game_name);
			statement_ranking.setString(3, game_name);
			rs = statement_ranking.executeQuery();
			rs.next();
			info.put("ranking", rs.getInt("ranking"));

			statement_userInfo = connection_userInfo.prepareStatement(sql_userInfo);
			statement_userInfo.setInt(1, user_id);
			statement_userInfo.setString(2, game_name);
			rs = statement_userInfo.executeQuery();
			rs.next();			
			info.put("game_name", rs.getString("game_name"));
			info.put("user_name", rs.getString("user_name"));
			info.put("user_id", rs.getInt("user_id"));
			info.put("point", rs.getInt("point"));
// 2015.10の仕様では提供する必要がない情報
//			info.put("create_date", rs.getTimestamp("create_date"));
//			info.put("update_date", rs.getTimestamp("update_date"));
//			info.put("final_date", rs.getTimestamp("final_date"));
//			info.put("count", rs.getInt("count"));

		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection_ranking != null) {
				try {
					connection_ranking.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (connection_userInfo != null) {
				try {
					connection_userInfo.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (statement_ranking != null) {
				try {
					statement_ranking.close();
				} catch (SQLException e) {
				}
			}
			if (statement_userInfo != null) {
				try {
					statement_userInfo.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return info;
	}
	public static JSONArray getHigher(
			String game_name, 
			int NUMBER_OF_TOP)
			throws Exception{
		String sql = "select *, user.name as user_name from score"
				+ " left join user"
				+ " on score.user_id = user.id"
				+ " where game_name = ?"
				+ " ORDER BY point DESC limit ?";
		
		PreparedStatement statement = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		JSONArray scores = new JSONArray();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, game_name);
			statement.setInt(2, NUMBER_OF_TOP);
			rs = statement.executeQuery();
			while (rs.next()) {
				JSONObject score = new JSONObject();
				score.put("game_name", rs.getString("game_name"));
				score.put("user_name", rs.getString("user_name"));
				score.put("point", rs.getInt("point"));
				scores.put(score);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // ignore
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		return scores;
	}
}

