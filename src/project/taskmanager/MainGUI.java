package project.taskmanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class MainGUI extends Application {
    private TaskManager manager = new TaskManager();
    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Manager");

        Button addButton = new Button("Add Task");
        Button showAllButton = new Button("Show All Tasks");
        Button showWorkButton = new Button("Show Work Tasks");
        Button showPersonalButton = new Button("Show Personal Tasks");
        Button showUrgentButton = new Button("Show Urgent Tasks");
        Button deleteButton = new Button("Delete Task");
        Button exitButton = new Button("Exit");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        addButton.setOnAction(e -> addTaskDialog());
        showAllButton.setOnAction(e -> showTasks(manager.getTasks()));
        showWorkButton.setOnAction(e -> showTasks(manager.getTasks().stream()
                .filter(t -> t instanceof WorkTask)
                .collect(Collectors.toList())));
        showPersonalButton.setOnAction(e -> showTasks(manager.getTasks().stream()
                .filter(t -> t instanceof PersonalTask)
                .collect(Collectors.toList())));
        showUrgentButton.setOnAction(e -> showTasks(manager.getTasks().stream()
                .filter(t -> t.getDueDate().isBefore(LocalDateTime.now().plusMinutes(60)))
                .collect(Collectors.toList())));
        deleteButton.setOnAction(e -> deleteTaskDialog());
        exitButton.setOnAction(e -> Platform.exit());

        VBox vbox = new VBox(10, addButton, showAllButton, showWorkButton, showPersonalButton, showUrgentButton,
                deleteButton, exitButton, outputArea);
        Scene scene = new Scene(vbox, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        startReminderChecker();
    }

    private void showTasks(List<Task> tasks) {
        outputArea.clear();
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks) {
            sb.append(task).append("\n");
        }
        outputArea.appendText(sb.toString());
    }

    private void addTaskDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText("Enter Task Details:");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Task Description");

        DatePicker dueDatePicker = new DatePicker(LocalDate.now());
        dueDatePicker.setPromptText("Due Date");

        TextField hourField = new TextField();
        hourField.setPromptText("Hour (0-23)");
        TextField minuteField = new TextField();
        minuteField.setPromptText("Minute (0-59)");

        ComboBox<Integer> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(1, 2, 3);
        priorityComboBox.setPromptText("Priority");

        ComboBox<Integer> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(1, 2);
        typeComboBox.setPromptText("Task Type (1=Work,2=Personal)");

        VBox content = new VBox(10, descriptionField, dueDatePicker, hourField, minuteField, priorityComboBox, typeComboBox);
        dialog.getDialogPane().setContent(content);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String description = descriptionField.getText().trim();
                    LocalDate date = dueDatePicker.getValue();
                    int hour = Integer.parseInt(hourField.getText().trim());
                    int minute = Integer.parseInt(minuteField.getText().trim());
                    Integer priority = priorityComboBox.getValue();
                    Integer type = typeComboBox.getValue();

                    if (description.isEmpty() || date == null || priority == null || type == null) {
                        showError("All fields are required.");
                        return null;
                    }

                    LocalDateTime dueDate = LocalDateTime.of(date, LocalTime.of(hour, minute));
                    Task task = (type == 1)
                            ? new WorkTask(description, dueDate, priority)
                            : new PersonalTask(description, dueDate, priority);
                    manager.addTask(task);
                    return task;
                } catch (Exception e) {
                    showError("Invalid input: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteTaskDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Task");
        dialog.setHeaderText("Enter Task ID to Delete:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                int taskId = Integer.parseInt(id.trim());
                manager.deleteTask(taskId);
            } catch (NumberFormatException e) {
                showError("Invalid ID");
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void startReminderChecker() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<Task> dueSoon = manager.getTasks().stream()
                        .filter(task -> {
                            LocalDateTime now = LocalDateTime.now();
                            return task.getDueDate().isAfter(now)
                                    && task.getDueDate().isBefore(now.plusMinutes(10));
                        })
                        .collect(Collectors.toList());

                for (Task task : dueSoon) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Task Reminder");
                        alert.setHeaderText("Task Due Soon!");
                        alert.setContentText(task.toString());
                        alert.showAndWait();
                    });
                }
            }
        }, 0, 60000); // Check every minute
    }
}
