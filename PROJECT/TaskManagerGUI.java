import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class TaskManagerGUI extends JFrame {


    private JTextField taskNameField;
    private JComboBox<String> priorityComboBox;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;
    private List<Task> taskList;
    private String currentUser;

    public TaskManagerGUI(String username) {
        this.currentUser = username;
        dbManager = new DatabaseManager();
        taskList = new ArrayList<>();

        setTitle("Task Manager");
        setSize(850, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new BackgroundImagePanel("bg.png");
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 450));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.GRAY));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("TASK MANAGER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(20));

        taskNameField = new JTextField();
        taskNameField.setBorder(BorderFactory.createTitledBorder("Task Name"));
        taskNameField.setMaximumSize(new Dimension(240, 60));
        leftPanel.add(taskNameField);
        leftPanel.add(Box.createVerticalStrut(10));

        String[] priorities = {"Low", "Medium", "High"};
        priorityComboBox = new JComboBox<>(priorities);
        priorityComboBox.setBorder(BorderFactory.createTitledBorder("Task Priority"));
        priorityComboBox.setMaximumSize(new Dimension(240, 70));
        leftPanel.add(priorityComboBox);
        leftPanel.add(Box.createVerticalStrut(20));

        JButton addButton = createStyledButon("Add");
        JButton completeButton = createStyledButon("Complete");
        JButton removeButton = createStyledButon("Remove");
        JButton logoutButton = createStyledButon("Logout");

        JPanel buttonPanel = new JPanel();
        addButton.setBounds(20, 10, 200, 40);
        completeButton.setBounds(20, 80, 200, 40);
        removeButton.setBounds(20, 110, 200, 40);
        logoutButton.setBounds(20, 160, 200, 40);

        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(completeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(logoutButton);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(buttonPanel);

        // Right Panel
        String[] columns = {"Task", "Status", "Priority"};
        tableModel = new DefaultTableModel(columns, 0);
        taskTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(taskTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Task List"));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(10, 20, 10, 10));
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        rightPanel.setOpaque(false);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Button Actions
        addButton.addActionListener(e -> showAddTaskDialog());
        completeButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.setValueAt("Completed", selectedRow, 1);
                Task task = taskList.get(selectedRow);
                task.setCompleted(true);
                dbManager.updateTaskCompletionStatus(task.getId(), true);
            }
        });
        removeButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                Task task = taskList.get(selectedRow);
                dbManager.deleteTaskFromDatabase(task.getId());
                taskList.remove(selectedRow);
                tableModel.removeRow(selectedRow);
            }
        });
        logoutButton.addActionListener(e -> {
            dispose();
            new loginGUI();
        });

        loadTasksFromDatabase();
        setVisible(true);
    }

    private JButton createStyledButon(String text) {
        return new RoundedButton(text);
    }

    class RoundedButton extends JButton {
        private Color normalColor = new Color(70, 130, 180);
        private Color hoverColor = new Color(100, 160, 220);
        private boolean hovered = false;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 14));
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 20));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics g2 = g.create();
            g2.setColor(hovered ? hoverColor : normalColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g2);
        }
    }

    private void showAddTaskDialog() {
        String taskTitle = taskNameField.getText().trim();
        String priorityStr = (String) priorityComboBox.getSelectedItem();

        if (!taskTitle.isEmpty()) {
            Task.Priority priority = Task.Priority.valueOf(priorityStr.toUpperCase());
            Task newTask = new Task(taskTitle, priority);
            dbManager.addTaskToDatabase(newTask);
            taskList.add(newTask);
            tableModel.addRow(new Object[]{
                newTask.getTitle(),
                newTask.isCompleted() ? "Completed" : "Pending",
                newTask.getPriority().name()
            });

            taskNameField.setText("");
            priorityComboBox.setSelectedIndex(0);
        }
    }

    private void loadTasksFromDatabase() {
        taskList = dbManager.getAllTasksFromDatabase();
        for (Task task : taskList) {
            tableModel.addRow(new Object[]{
                task.getTitle(),
                task.isCompleted() ? "Completed" : "Pending",
                task.getPriority().name()
            });
        }
    }
}

// BackgroundImagePanel moved outside constructor
class BackgroundImagePanel extends JPanel {
    private final Image backgroundImage;

    public BackgroundImagePanel(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
