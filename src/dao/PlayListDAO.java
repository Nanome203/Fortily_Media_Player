package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.PlayListItem;

public class PlayListDAO {
	public final String urlDB = "jdbc:sqlite:E:\\Program\\sqlLite\\music_player.db";

	public void addPlayList(String playListName) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "insert into playlist values (null, ?, ?, ?)";
		
		try {
			conn = DriverManager.getConnection(urlDB);
			stmt = conn.prepareStatement(sql);
			
			stmt.setString(1,playListName);
			stmt.setString(2,"/assets/images/musical-symbol-icon.jpg");
			stmt.setInt(3,0);
			stmt.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn, stmt, null);
		}
	}
	
	public List<PlayListItem> getAllPlayList() throws SQLException {
		List<PlayListItem> listItems = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from playlist";
		
		
		try {
			conn = DriverManager.getConnection(urlDB);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			listItems = new ArrayList<PlayListItem>();
			while(rs.next()) {
				String playlistName = rs.getString(2);
				String sourceImg = rs.getString(3);
				int numOfItem = rs.getInt(4);
				
				PlayListItem playListItem = new PlayListItem();
				playListItem.setPlayListName(playlistName);
				playListItem.setImgSrc(sourceImg);
				playListItem.setNumOfItems(numOfItem);
				
				listItems.add(playListItem);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			close(conn, stmt, rs);
		}
		
		return listItems;
		
	}

	private void close(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
		if(conn != null) conn.close();
		if(stmt != null) stmt.close();
		if(rs != null) rs.close();
		
	}
	
	public static void main(String[] args) throws SQLException {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        System.out.println(formattedDate);
	}

}
