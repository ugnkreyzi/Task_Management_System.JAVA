import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Vector;

public class TaskManager {
    private Vector<Task> tasks = new Vector<>();
    private static final String FILE_NAME = "taskproj.txt";
    private final DatabaseManager dbManager = new DatabaseManager();

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/your_database";  // Change this URL as needed
    private static final String DB_USER = "root";  // Add your username here
    private static final String DB_PASSWORD = "cyril01";  // Add your password here

    public TaskManager() {
        loadTasks();  // Load from file initially
        // Optionally load from database instead:
        // loadTasksFromDatabase();
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        addTaskToDatabase(task);  // Save to database too
    }

    public Vector<Task> getTasks() {
        return tasks;
    }

    public void markTaskAsCompleted(int taskId) {
        System.out.println("Attempting to mark task as completed with ID: " + taskId);
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Database connection successful.");
    
            String query = "UPDATE tasks SET completed = TRUE WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, taskId);
                int rowsUpdated = stmt.executeUpdate();
    
                // Check if the update was successful
                if (rowsUpdated > 0) {
                    System.out.println("Task marked as completed. Rows updated: " + rowsUpdated);
                } else {
                    System.out.println("No rows updated. Task may not exist or already be completed.");
                }
            } catch (SQLException e) {
                System.err.println("Error executing update query:");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to the database:");
            e.printStackTrace();
        }
    }
    

    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            dbManager.deleteTaskFromDatabase(task.getId()); // Remove from DB
            tasks.remove(index); // Remove from memory
            saveTasks(); // Update local file if needed
        }
    }
    

    private void saveTasks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(tasks);
            System.out.println("Tasks saved to file.");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    private void loadTasks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            Object obj = in.readObject();
            if (obj instanceof Vector<?>) {
                @SuppressWarnings("unchecked")
                Vector<Task> taskVector = (Vector<Task>) obj;
                tasks = taskVector;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Task file not found, starting fresh.");
            tasks = new Vector<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            tasks = new Vector<>();
        }
    }

    private void addTaskToDatabase(Task task) {
        dbManager.addTaskToDatabase(task);
    }

    public void loadTasksFromDatabase() {
        List<Task> dbTasks = dbManager.getAllTasksFromDatabase();
        tasks = dbTasks != null ? new Vector<>(dbTasks) : new Vector<>();
    }

    public Vector<Task> getTasksFromDatabase() {
        return new Vector<>(dbManager.getAllTasksFromDatabase());
    }
}
