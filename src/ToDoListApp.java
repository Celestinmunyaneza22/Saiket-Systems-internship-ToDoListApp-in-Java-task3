import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Task implements Serializable {
    private String title;
    private boolean isCompleted;

    public Task(String title) {
        this.title = title;
        this.isCompleted = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void markCompleted() {
        isCompleted = true;
    }

    @Override
    public String toString() {
        return (isCompleted ? "[✔] " : "[ ] ") + title;
    }
}


// Main GUI class
public class ToDoListApp extends JFrame{
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskJList;
    private JTextField taskInput;

    private static final String FILE_NAME = "tasks.dat";

    public ToDoListApp() {
        setTitle("To-Do List Application");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        taskListModel = new DefaultListModel<>();
        taskJList = new JList<>(taskListModel);
        taskJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskJList);

        taskInput = new JTextField(20);

        JButton addButton = new JButton("Add Task");
        JButton completeButton = new JButton("Mark as Complete");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton saveButton = new JButton("Save Tasks");
        JButton loadButton = new JButton("Load Tasks");

        JPanel inputPanel = new JPanel();
        inputPanel.add(taskInput);
        inputPanel.add(addButton);

        JPanel controlPanel = new JPanel();
        controlPanel.add(completeButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);

        // Add Task
        addButton.addActionListener(e -> {
            String title = taskInput.getText().trim();
            if (!title.isEmpty()) {
                Task newTask = new Task(title);
                taskListModel.addElement(newTask);
                taskInput.setText("");
            }
        });

        // Mark as Complete
        completeButton.addActionListener(e -> {
            int selectedIndex = taskJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task selectedTask = taskListModel.getElementAt(selectedIndex);
                selectedTask.markCompleted();
                taskJList.repaint();
            }
        });

        // Edit Task
        editButton.addActionListener(e -> {
            int selectedIndex = taskJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task selectedTask = taskListModel.getElementAt(selectedIndex);
                String newTitle = JOptionPane.showInputDialog(this, "Edit task title:", selectedTask.getTitle());
                if (newTitle != null && !newTitle.trim().isEmpty()) {
                    selectedTask.setTitle(newTitle.trim());
                    taskJList.repaint();
                }
            }
        });

        // Delete Task
        deleteButton.addActionListener(e -> {
            int selectedIndex = taskJList.getSelectedIndex();
            if (selectedIndex != -1) {
                taskListModel.remove(selectedIndex);
            }
        });

        // Save Tasks to File
        saveButton.addActionListener(e -> {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                ArrayList<Task> tasksToSave = new ArrayList<>();
                for (int i = 0; i < taskListModel.size(); i++) {
                    tasksToSave.add(taskListModel.getElementAt(i));
                }
                out.writeObject(tasksToSave);
                JOptionPane.showMessageDialog(this, "Tasks saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving tasks: " + ex.getMessage());
            }
        });

        // Load Tasks from File
        loadButton.addActionListener(e -> {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                ArrayList<Task> loadedTasks = (ArrayList<Task>) in.readObject();
                taskListModel.clear();
                for (Task task : loadedTasks) {
                    taskListModel.addElement(task);
                }
                JOptionPane.showMessageDialog(this, "Tasks loaded successfully.");
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error loading tasks: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ToDoListApp().setVisible(true);
        });
    }
}
