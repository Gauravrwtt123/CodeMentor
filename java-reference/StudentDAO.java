public class StudentDAO {
    // Insert Student
    public boolean registerStudent(String name, String email) {
        String sql = "INSERT INTO students (name, email) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch Chat History
    public List<String> getChatHistory(int studentId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT message FROM chat_logs WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                history.add(rs.getString("message"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return history;
    }
}