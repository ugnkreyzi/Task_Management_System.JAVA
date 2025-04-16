// No package declaration here
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;




public class DatabaseManager {
    // Existing code...

    public static void main(String[] args) {
        // This can be used to test the DatabaseManager class functionality
        DatabaseManager dbManager = new DatabaseManager();
        // Call methods to test it here
        System.out.println("DatabaseManager is working");
    }

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/task_management_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "cyril01";

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


      public static boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if user exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

 
    


    public static boolean registerUser(String username, String password, String securityQuestion, String securityAnswer) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Hash the security answer before storing it (for better security)
            String hashedAnswer = hashAnswer(securityAnswer);
            
            // SQL query to insert user along with the security question and answer
            String query = "INSERT INTO users (username, password, security_question, security_answer) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password); // You should hash the password as well in a real-world scenario
            stmt.setString(3, securityQuestion);
            stmt.setString(4, hashedAnswer);
            
            // Execute the update
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            // username already exists
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String hashAnswer(String answer) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(answer.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        return null;
    }
}

    








    public void addTaskToDatabase(Task task) {
        String query = "INSERT INTO tasks (TASK, STATUS, PRIORITY) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, task.getTitle());
            statement.setString(2, task.isCompleted() ? "Completed" : "Pending");
            statement.setString(3, task.getPriority().name());
            
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    task.setId(generatedId); // Set the ID of the task object
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public void updateTaskCompletionStatus(int taskId, boolean isCompleted) {
        String query = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, isCompleted ? "Completed" : "Pending");
            stmt.setInt(2, taskId);
    
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public List<Task> getAllTasksFromDatabase() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT TASK, STATUS, PRIORITY FROM tasks";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String taskTitle = resultSet.getString("TASK");
                String status = resultSet.getString("STATUS");
                String priorityStr = resultSet.getString("PRIORITY");
                
                Task.Priority priority = Task.Priority.valueOf(priorityStr.toUpperCase());
                boolean isCompleted = "Completed".equals(status);
                
                Task task = new Task(id, taskTitle, priority, isCompleted); // use full constructor with ID
                task.setCompleted(isCompleted);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }

    public void deleteTaskFromDatabase(int taskId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Attempting to delete task with ID: " + taskId); // Debugging line
            String query = "DELETE FROM tasks WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, taskId);
                int rowsDeleted = stmt.executeUpdate();
                System.out.println("Rows deleted: " + rowsDeleted); // Debugging line
                if (rowsDeleted > 0) {
                    System.out.println("Task deleted from database.");
                } else {
                    System.out.println("No task found with the given ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    

    
}
