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
import java.util.concurrent.TimeUnit;
import model.SongMetadata;

public class SongDAO {
	public static final String urlDB = "jdbc:sqlite:"
			+ (new File("").getAbsolutePath().concat("\\src\\database\\media_player.db"));

	public static Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(urlDB);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return conn;
	}

	public List<SongMetadata> getAllMedia() throws SQLException {

		List<SongMetadata> listSongs = new ArrayList<SongMetadata>();
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from Recent_Media order by LastDateOpened DESC";

		try {
			connection = getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				SongMetadata song = new SongMetadata();
				String pathMedia = rs.getString(1);
				String dateTime = rs.getString(2);

				song.setPathname(pathMedia);
				song.setLastDayOpened(dateTime);

				listSongs.add(song);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}

		return listSongs;

	}

	// Trong trường hợp file kiểm tra tồn tại trong csdl nhưng không tồn tại trên
	// máy

	// thì xoá thông tin file khỏi csdl

	// Trong trường hợp có tồn tại trên máy, kiểm tra có thông tin file trong csdl
	// hay không

	// Nếu có thì cập nhật lại path (trong trường hợp path của file mình kiểm tra và
	// path trong csdl khác nhau)

	// Rồi load thông tin file lên

	public String convertDurationMillis(Integer getDurationInMillis) {
		int getDurationMillis = getDurationInMillis;

		String convertHours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(getDurationMillis));
		String convertMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationMillis) -
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(getDurationMillis)));
		String convertSeconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationMillis) -
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationMillis)));

		String getDuration = convertHours + ":" + convertMinutes + ":" + convertSeconds;

		return getDuration;

	}

	public void addMedia(File getFile, String dateTime) throws SQLException {
		if (!getFile.exists() || getFile.isDirectory()) {
			System.out.println("File does not exist!");

			return;
		}

		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String sqlInsert = "INSERT INTO Recent_Media VALUES(?,?);";

		try {
			conn = getConnection();

			String getMediaPath = getFile.getAbsolutePath();

			preStmt = conn.prepareStatement(sqlInsert);
			preStmt.setString(1, getMediaPath);
			preStmt.setString(2, dateTime);

			preStmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				close(conn, preStmt, rs);
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

	}

	private void close(Connection conn, PreparedStatement preStmt, ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
		if (preStmt != null)
			preStmt.close();
		if (conn != null)
			conn.close();
	}

	public int deleteMedia(String getPath) throws SQLException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		String sqlDelete = "DELETE FROM Recent_Media WHERE PathMedia = ?;";

		try {
			conn = getConnection();
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

	public boolean checkExist(File selectedFile) throws SQLException {

		Connection conn = null;
		PreparedStatement checkStmt = null;
		ResultSet rs = null;
		String sqlCheck = "SELECT COUNT(*) FROM Recent_Media WHERE PathMedia = ?;";

		try {
			conn = getConnection();
			checkStmt = conn.prepareStatement(sqlCheck);
			String getMediaPath = selectedFile.getAbsolutePath();
			checkStmt.setString(1, getMediaPath);
			rs = checkStmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
			if (checkStmt != null)
				checkStmt.close();
		}

		return false;
	}

	public void updateDate(File selectedFile, String currentDateTime) throws SQLException {
		Connection conn = null;
		PreparedStatement checkStmt = null;
		String sqlCheck = "UPDATE Recent_Media SET LastDateOpened = ? WHERE PathMedia = ?;";

		try {
			conn = getConnection();
			checkStmt = conn.prepareStatement(sqlCheck);
			String getMediaPath = selectedFile.getAbsolutePath();
			checkStmt.setString(1, currentDateTime);
			checkStmt.setString(2, getMediaPath);
			checkStmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
			if (checkStmt != null)
				checkStmt.close();
		}

	}
}
