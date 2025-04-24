package project.taskmanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
    private TableView<Task> tableView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Manager");

        Label titleLabel = new Label("Task Manager");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setAlignment(Pos.CENTER);

        Button addButton = createButton("Add Task");
        Button showAllButton = createButton("Show All Tasks");
        Button showWorkButton = createButton("Show Work Tasks");
        Button showPersonalButton = createButton("Show Personal Tasks");
        Button showUrgentButton = createButton("Show Urgent Tasks");
        Button deleteButton = createButton("Delete Task");
        Button exitButton = createButton("Exit");

        tableView = new TableView<>();
        setupTableColumns();

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

        VBox vbox = new VBox(12, titleLabel, addButton, showAllButton, showWorkButton, showPersonalButton,
                showUrgentButton, deleteButton, exitButton, tableView);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 15;");

        Scene scene = new Scene(vbox, 800, 650);
        primaryStage.setScene(scene);
        primaryStage.show();

        startReminderChecker();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(300);
        button.setFont(Font.font("Arial", 14));
        return button;
    }

    private void setupTableColumns() {
        TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Task, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            if (data.getValue() instanceof WorkTask)
                return new ReadOnlyStringWrapper("Work");
            if (data.getValue() instanceof PersonalTask)
                return new ReadOnlyStringWrapper("Personal");
            return new ReadOnlyStringWrapper("Unknown");
        });

        TableColumn<Task, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Task, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDueDate().toString()));

        TableColumn<Task, Integer> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));

        tableView.getColumns().addAll(idCol, typeCol, descCol, dueDateCol, priorityCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void showTasks(List<Task> tasks) {
        ObservableList<Task> observableTasks = FXCollections.observableArrayList(tasks);
        tableView.setItems(observableTasks);
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

        ComboBox<String> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll("High", "Medium", "Low");
        priorityComboBox.setPromptText("Priority");

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Work", "Personal");
        typeComboBox.setPromptText("Task Type");

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
                    String priorityStr = priorityComboBox.getValue();
                    String typeStr = typeComboBox.getValue();

                    if (description.isEmpty() || date == null || priorityStr == null || typeStr == null) {
                        showError("All fields are required.");
                        return null;
                    }

                    LocalDateTime dueDate = LocalDateTime.of(date, LocalTime.of(hour, minute));
                    int priority = switch (priorityStr) {
                        case "High" -> 1;
                        case "Medium" -> 2;
                        case "Low" -> 3;
                        default -> 2;
                    };

                    Task task = switch (typeStr) {
                        case "Work" -> new WorkTask(description, dueDate, priority);
                        case "Personal" -> new PersonalTask(description, dueDate, priority);
                        default -> null;
                    };

                    if (task != null) {
                        manager.addTask(task);
                        return task;
                    } else {
                        showError("Invalid task type.");
                        return null;
                    }
                } catch (Exception e) {
                    showError("Invalid input: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> showTasks(manager.getTasks()));
    }

    private void deleteTaskDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Task");
        dialog.setHeaderText("Enter Task ID to Delete:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                int taskId = Integer.parseInt(id.trim());
                manager.deleteTask(taskId);
                showTasks(manager.getTasks());
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
        }, 0, 60_000); // Check every minute
    }
}
