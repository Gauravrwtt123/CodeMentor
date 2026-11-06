package com.codementor.dao;
import com.codementor.model.ChatMessage; import com.codementor.util.DBConnection;
import java.sql.*; import java.util.*;
/** JDBC DAO: parameterized SQL and try-with-resources for reliable resource cleanup. */
public class ChatDAO {
 public void save(int userId,String prompt,String response)throws SQLException{
  String sql="INSERT INTO chat_logs(user_id,prompt,response) VALUES(?,?,?)";
  try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
   ps.setInt(1,userId);ps.setString(2,prompt);ps.setString(3,response);ps.executeUpdate();
  }
 }
 public List<ChatMessage> findRecentByUser(int userId,int limit)throws SQLException{
  int safeLimit=Math.max(1,Math.min(limit,100));
  String sql="SELECT id,user_id,prompt,response,created_at FROM chat_logs WHERE user_id=? ORDER BY created_at DESC LIMIT ?";
  List<ChatMessage> rows=new ArrayList<>();
  try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
   ps.setInt(1,userId);ps.setInt(2,safeLimit);
   try(ResultSet rs=ps.executeQuery()){while(rs.next()){
    Timestamp ts=rs.getTimestamp("created_at");
    rows.add(new ChatMessage(rs.getLong("id"),rs.getInt("user_id"),rs.getString("prompt"),rs.getString("response"),ts==null?null:ts.toLocalDateTime()));
   }}
  } return rows;
 }
}
