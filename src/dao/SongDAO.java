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

import model.SongMetadata;

public class SongDAO {
	public final String urlDB = "jdbc:sqlite:E:\\Program\\sqlLite\\music_player.db";	
	Connection connection = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	
	public List<SongMetadata> getAllMedia() throws SQLException   {
		List<SongMetadata> listSongs = new ArrayList<SongMetadata>();
		String sql = "select * from Recent_Media order by LastDateOpened";
		try {
			connection = DriverManager.getConnection(urlDB);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				SongMetadata song = new SongMetadata();
				String pathMedia = rs.getString(1);
				String dateTime = rs.getString(2);
				song.setPathname(pathMedia);
				song.setLastDayOpened(dateTime);
				
				listSongs.add(song);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(connection != null)
				connection.close();
		}
		
		return listSongs;
		
	}
	
	
	
	public int addMedia(File getFile, String dateTime) throws SQLException {
	    if (!getFile.exists() || getFile.isDirectory()) {
	        System.out.println("File does not exist!");
	        return -1;
	    }

	    int success = 0;
	    Connection conn = null;
	    PreparedStatement preStmt = null;
	    ResultSet rs = null;
	    String sqlInsert = "INSERT INTO Recent_Media VALUES(?,?);";

	    try {
	        conn = DriverManager.getConnection(urlDB);

	       
	        String getMediaPath = getFile.getAbsolutePath();
	       

	   
	        preStmt = conn.prepareStatement(sqlInsert);
	        preStmt.setString(1, getMediaPath);
	        preStmt.setString(2, dateTime);
	        preStmt.execute();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        success = 1;
	    } finally {
	        if (rs != null) rs.close();
	        if (preStmt != null) preStmt.close();
	        if (conn != null) conn.close();
	    }

	    return success;
	}



	public int deleteMedia(String getPath) throws SQLException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		String sqlDelete = "DELETE FROM Recent_Media WHERE PathMedia=?;";

		try {
			conn = DriverManager.getConnection(urlDB);
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


	public boolean checkExist(File selectedFile) {

		Connection conn = null;
		PreparedStatement checkStmt = null;
		String sqlCheck = "SELECT COUNT(*) FROM Recent_Media WHERE PathMedia = ?;";

		    
	    try {
			conn = DriverManager.getConnection(sqlCheck);
			 checkStmt = conn.prepareStatement(sqlCheck);
		    String getMediaPath = selectedFile.getAbsolutePath();
		    checkStmt.setString(1, getMediaPath);
		    rs = checkStmt.executeQuery();
		
		    if (rs.next() && rs.getInt(1) > 0) {
		       return false;
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		return true;
	}



	public void updateDate(File selectedFile, String currentDateTime) {
		Connection conn = null;
		PreparedStatement checkStmt = null;
		String sqlCheck = "UPDATE Recent_Media SET LastDateOpened = ? WHERE PathMedia = ?;";

		    
	 
			try {
				conn = DriverManager.getConnection(sqlCheck);
				checkStmt = conn.prepareStatement(sqlCheck);
			    String getMediaPath = selectedFile.getAbsolutePath();
			    checkStmt.setString(1, currentDateTime);
			    checkStmt.setString(2, getMediaPath);
			    checkStmt.execute();
			    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	 
		
	}
}
