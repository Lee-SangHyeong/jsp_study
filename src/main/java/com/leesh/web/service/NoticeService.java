package com.leesh.web.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.leesh.web.entity.Notice;
import com.leesh.web.entity.NoticeView;

public class NoticeService {
	
	public int removeNoticeAll(int[] ids){
		
		return 0;
	}
	
	public int pubNoticeAll(int[] openIds, int[] closeIds){
		//정수형 배열 --> 문자형 List 변환
		List<String> listOpenIds = new ArrayList<>();
		for(int i=0; i<openIds.length; i++)
			listOpenIds.add(String.valueOf(openIds[i]));

		List<String> listCloseIds = new ArrayList<>();
		for(int i=0; i<closeIds.length; i++)
			listCloseIds.add(String.valueOf(closeIds[i]));
		
		return pubNoticeAll(listOpenIds, listCloseIds);
	}

	public int pubNoticeAll(List<String> openIds, List<String> closeIds){
		//List --> 문자형CSV 변환
		String openIdsCSV = String.join(",", openIds);
		String closeIdsCSV = String.join(",", closeIds);
		
		return pubNoticeAll(openIdsCSV, closeIdsCSV);
	}

	public int pubNoticeAll(String openIdsCSV, String closeIdsCSV){
		int result = 0;
		
		String sqlOpen = String.format("UPDATE TT_NOTICE SET PUB = 1 WHERE ID IN (%s)", openIdsCSV);
		String sqlClose = String.format("UPDATE TT_NOTICE SET PUB = 0 WHERE ID IN (%s)", closeIdsCSV);
		
		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
			Statement stOpen = con.createStatement();
			result += stOpen.executeUpdate(sqlOpen);
			
			Statement stClose = con.createStatement();
			result += stClose.executeUpdate(sqlClose);
			
			stOpen.close();
			stClose.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return result;	
	}
	
	public int insertNotice(Notice notice){
		int result = 0;
		
		String sql = "INSERT INTO HSTEST.TT_NOTICE "
				+ "       (ID, TITLE, WRITER_ID, CONTENT, FILES, PUB) "
				+ "VALUES (SEQ_NO.NEXTVAL, ?, ?, ?, ?, ?)";
		
		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, notice.getTitle());
			st.setString(2, notice.getWriterId());
			st.setString(3, notice.getContent());
			st.setString(4, notice.getFiles());
			st.setBoolean(5, notice.isPub());
			
			result = st.executeUpdate();
			
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return result;	
	}
	
	public int deleteNotice(int id){
		return 0;
	}

	public int deleteNoticeAll(int[] ids){
		int result = 0;
		String params = "";
		for(int i=0; i<ids.length; i++ ) {
			params += ids[i];
			if(i < ids.length-1) params += ",";
		}
		
		String sql = "DELETE FROM TT_NOTICE WHERE ID IN ("+params+")";
		System.out.printf("deleteNoticeAll sql: %s\n", sql);
		
		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
			Statement st = con.createStatement();
			result = st.executeUpdate(sql);
			
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return result;
	}
	
	public int updateNotice(Notice notice){
	
		return 0;
	}
	
	public List<Notice> getNoticeNewestList(){
		return null;
	}
	
	public List<NoticeView> getNoticeList(){
		return getNoticeList("title", "", 1);
	}

	public List<NoticeView> getNoticeList(int page){
		return getNoticeList("title", "", page);
	}

	public List<NoticeView> getNoticeList(String field, String query, int page){
		List<NoticeView> list = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT N.*, ");
		sb.append("      (SELECT COUNT(*) FROM TT_COMMENT C WHERE  C.NOTICE_ID = N.ID) AS COMMENT_CNT ");
		sb.append("FROM  ( ");
		sb.append("       SELECT ROW_NUMBER() OVER(PARTITION BY 1 ORDER BY REGDATE DESC) AS NUM, ");
		sb.append("              A.* ");
		sb.append("       FROM   TT_NOTICE A ");
		sb.append("       WHERE ").append(field).append(" LIKE ? ");
		sb.append("      ) N ");
		sb.append("WHERE N.NUM BETWEEN ? AND ? ");
		//rs = stmt.executeQuery(sb.toString());
		//System.out.println("getNoticeList: " + sb.toString());
		
		//1,  11, 21, 31 -> an = 1 + (page-1)*10
		//10, 20, 20, 30 -> page*10 
		
		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
	
			PreparedStatement st = con.prepareStatement(sb.toString());
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10 );
			//System.out.println("getNoticeList: " + st.toString());
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){
				int id = rs.getInt("ID");
				String title = rs.getString("TITLE");
				String writerId = rs.getString("WRITER_ID");
				Date regDate = rs.getDate("REGDATE");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				int commentCnt = rs.getInt("COMMENT_CNT");
				boolean pub = rs.getBoolean("PUB");
				
				NoticeView notice = new NoticeView(id,title,writerId, regDate, hit,files, content, pub, commentCnt);
				list.add(notice);
			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return list;
	}

	public List<NoticeView> getNoticePubList(String field, String query, int page) {
		List<NoticeView> list = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT N.*, ");
		sb.append("      (SELECT COUNT(*) FROM TT_COMMENT C WHERE  C.NOTICE_ID = N.ID) AS COMMENT_CNT ");
		sb.append("FROM  ( ");
		sb.append("       SELECT ROW_NUMBER() OVER(PARTITION BY 1 ORDER BY REGDATE DESC) AS NUM, ");
		sb.append("              A.* ");
		sb.append("       FROM   TT_NOTICE A ");
		sb.append("       WHERE ").append(field).append(" LIKE ? ");
		sb.append("      ) N ");
		sb.append("WHERE N.PUB = 1 ");
		sb.append("AND   N.NUM BETWEEN ? AND ? ");
		//rs = stmt.executeQuery(sb.toString());
		//System.out.println("getNoticeList: " + sb.toString());
		
		//1,  11, 21, 31 -> an = 1 + (page-1)*10
		//10, 20, 20, 30 -> page*10 
		
		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
	
			PreparedStatement st = con.prepareStatement(sb.toString());
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10 );
			//System.out.println("getNoticeList: " + st.toString());
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){
				int id = rs.getInt("ID");
				String title = rs.getString("TITLE");
				String writerId = rs.getString("WRITER_ID");
				Date regDate = rs.getDate("REGDATE");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				int commentCnt = rs.getInt("COMMENT_CNT");
				boolean pub = rs.getBoolean("PUB");
				
				NoticeView notice = new NoticeView(id,title,writerId, regDate, hit,files, content, pub, commentCnt);
				list.add(notice);
			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return list;
	}
	
	public int getNoticeCount(){
		return getNoticeCount("title", "");
	}
	
	public int getNoticeCount(String field, String query){
		int count = 0;

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*) AS CNT "); 
		sb.append("FROM  ( ");
		sb.append("       SELECT ROW_NUMBER() OVER(PARTITION BY 1 ORDER BY REGDATE DESC) AS NUM, ");
		sb.append("              A.* ");
		sb.append("       FROM   TT_NOTICE A ");
		sb.append("       WHERE ").append(field).append(" LIKE ? ");
		sb.append("      ) N ");
		
		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
	
			PreparedStatement st = con.prepareStatement(sb.toString());
			st.setString(1, "%"+query+"%");
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){
				count = rs.getInt("CNT");
			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return count;
	}
	
	public Notice getNotice(int id) {
		Notice notice = null;
		
		String sql = "SELECT * FROM TT_NOTICE WHERE ID = ?";
		
		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
	
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){
				int lid = rs.getInt("ID");
				String title = rs.getString("TITLE");
				String writerId = rs.getString("WRITER_ID");
				Date regDate = rs.getDate("REGDATE");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				boolean pub = rs.getBoolean("PUB");
				
				notice = new Notice(lid,title,writerId, regDate, hit,files, content, pub);
			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return notice;
	}
	
	public Notice getNextNotice(int id) {
		Notice notice = null;
		
		String sql = "SELECT A.* "
				+ "   FROM   TT_NOTICE A "
				+ "   WHERE  A.ID = "
				+ "        ( "
				+ "          SELECT ID "
				+ "          FROM   TT_NOTICE "
				+ "          WHERE  REGDATE > (SELECT REGDATE FROM TT_NOTICE WHERE ID = ?) "
				+ "          AND    ROWNUM <= 1 "
				+ "    )";

		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
	
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){
				int lid = rs.getInt("ID");
				String title = rs.getString("TITLE");
				String writerId = rs.getString("WRITER_ID");
				Date regDate = rs.getDate("REGDATE");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				boolean pub = rs.getBoolean("PUB");
				
				notice = new Notice(lid,title,writerId, regDate, hit,files, content, pub);
			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return notice;
	}
	
	public Notice getPrevNotice(int id) {
		Notice notice = null;
		
		String sql = "SELECT A.* "
				+ "   FROM   TT_NOTICE A "
				+ "   WHERE  A.ID = "
				+ "        ( "
				+ "          SELECT ID "
				+ "          FROM  (SELECT ID, REGDATE FROM TT_NOTICE ORDER BY REGDATE DESC) "
				+ "          WHERE  REGDATE > (SELECT REGDATE FROM TT_NOTICE WHERE ID = ?) "
				+ "          AND    ROWNUM <= 1 "
				+ "    )";

		String url = "jdbc:oracle:thin:@192.168.30.7:5301/hsdb";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "HSTEST", "HSTEST");
	
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){
				int lid = rs.getInt("ID");
				String title = rs.getString("TITLE");
				String writerId = rs.getString("WRITER_ID");
				Date regDate = rs.getDate("REGDATE");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				boolean pub = rs.getBoolean("PUB");
				
				notice = new Notice(lid,title,writerId, regDate, hit,files, content, pub);
			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return notice;
	}

}
