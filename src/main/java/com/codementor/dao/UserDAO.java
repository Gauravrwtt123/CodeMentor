package com.codementor.dao;
import com.codementor.model.User; import com.codementor.util.DBConnection;
import java.sql.*; import org.mindrot.jbcrypt.BCrypt;
public class UserDAO {
 public User register(String name,String email,String password) throws SQLException {
  String sql="INSERT INTO users(name,email,password_hash) VALUES(?,?,?)";
  try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
   p.setString(1,name.trim());p.setString(2,email.trim().toLowerCase());p.setString(3,BCrypt.hashpw(password,BCrypt.gensalt()));
   p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){if(r.next())return new User(r.getInt(1),name,email);}
  } return null;
 }
 public User authenticate(String email,String password) throws SQLException {
  String sql="SELECT id,name,email,password_hash FROM users WHERE email=?";
  try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setString(1,email.trim().toLowerCase());try(ResultSet r=p.executeQuery()){
    if(r.next()&&BCrypt.checkpw(password,r.getString("password_hash")))return new User(r.getInt("id"),r.getString("name"),r.getString("email"));
   }
  }return null;
 }
}
