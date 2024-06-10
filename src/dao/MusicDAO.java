package dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MusicDAO {
	private final String pathdb = "jdbc:sqlite:"
			+ (new File("").getAbsolutePath().concat("\\src\\database\\media_player.db"));

	public List<File> getMediaList() throws SQLException {
		List<File> getList = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String sqlGetList = "SELECT Media FROM Favorite;";

		try {
			conn = DriverManager.getConnection(pathdb);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlGetList);

			while (rs.next()) {
				File getMediaFile = new File(rs.getString(1));
				if (getMediaFile != null) {
					getList.add(getMediaFile);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}

		return getList;
	}

	public int insertMedia(File getFile) throws SQLException {
		if (!getFile.exists() || getFile.isDirectory()) {
			System.out.println("File does not exist!");
			return -1;
		}

		int success = 0;
		Connection conn = null;
		PreparedStatement preStmt = null;
		String sqlInsert = "INSERT INTO Favorite VALUES(?);";

		try {
			conn = DriverManager.getConnection(pathdb);
			preStmt = conn.prepareStatement(sqlInsert);

			String getMediaPath = getFile.getAbsolutePath();
			preStmt.setString(1, getMediaPath);

			preStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			success = 1;
		} finally {
			if (conn != null)
				conn.close();
			if (preStmt != null)
				preStmt.close();
		}

		return success;
	}

	public int deleteMedia(String getPath) throws SQLException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		String sqlDelete = "DELETE FROM Favorite WHERE Media=?;";

		try {
			conn = DriverManager.getConnection(pathdb);
			preStmt = conn.prepareStatement(sqlDelete);
			preStmt.setString(1, getPath);
			preStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (conn != null)
				conn.close();
			if (preStmt != null)
				preStmt.close();
		}
		return 0;
	}
}
