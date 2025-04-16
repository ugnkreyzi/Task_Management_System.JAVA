import java.sql.ResultSet;

public class Task {
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    private int id;
    private String title;
    private Priority priority;
    private boolean completed;

    // Constructor with ID
    public Task(int id, String title, Priority priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.completed = completed;
    }

    // Constructor without ID (used when creating new tasks)
    public Task(String title, Priority priority) {
        this.title = title;
        this.priority = priority;
        this.completed = false;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // This method will be used to return a readable string for the status
    public String getStatus() {
        return completed ? "Completed" : "Pending";
    }

    // This method will be used to update the status after a task is marked as completed
    public void markCompleted() {
        this.completed = true;
    }

    // This method will be used for updating the status to 'Pending' if the task is uncompleted
    public void markPending() {
        this.completed = false;
    }

    // Override toString method for easy display (optional)
    @Override
    public String toString() {
        return "Task{id=" + id + ", title='" + title + "', priority=" + priority + ", completed=" + completed + "}";
    }
}
