package project.taskmanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class TaskManagerGUI extends Application {

    private TaskManager manager = new TaskManager();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox layout = new VBox(10);

        Label title = new Label("Task Manager");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button addButton = new Button("Add Task");
        Button showButton = new Button("Show All Tasks");
        Button deleteButton = new Button("Delete Task");

        addButton.setOnAction(e -> openAddTaskDialog());

        showButton.setOnAction(e -> showAllTasks());

        deleteButton.setOnAction(e -> openDeleteTaskDialog());

        layout.getChildren().addAll(title, addButton, showButton, deleteButton);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openAddTaskDialog() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Add Task");
        inputDialog.setHeaderText("Enter Task Description");
        inputDialog.showAndWait().ifPresent(description -> {
            if (!description.isEmpty()) {
                try {
                    Task task = new WorkTask(description, LocalDateTime.now().plusHours(1), 1);
                    manager.addTask(task);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Task Added: " + description);
                    alert.show();
                } catch (InvalidTaskException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Task: " + e.getMessage());
                    alert.show();
                }
            }
        });
    }

    private void showAllTasks() {
        ListView<String> listView = new ListView<>();
        try {
            for (Task task : manager.getTasks()) {
                listView.getItems().add(task.toString());
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading tasks: " + e.getMessage());
            alert.show();
        }

        VBox layout = new VBox(listView);
        Scene scene = new Scene(layout, 300, 400);
        Stage window = new Stage();
        window.setTitle("All Tasks");
        window.setScene(scene);
        window.show();
    }

    private void openDeleteTaskDialog() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Delete Task");
        inputDialog.setHeaderText("Enter Task ID to delete:");
        inputDialog.showAndWait().ifPresent(id -> {
            try {
                int taskId = Integer.parseInt(id);
                manager.deleteTask(taskId);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Task Deleted: " + id);
                alert.show();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid ID");
                alert.show();
            }
        });
    }
}
