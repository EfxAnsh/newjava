package project.taskmanager;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskManager {
    private static final String FILE_PATH = "tasks.txt";
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = loadTasks();
        removeExpiredTasks();
        startReminderService();
    }

    // Use this method to fetch the list of tasks
    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        scheduleReminder(task);
    }

    public void showTasks() {
        removeExpiredTasks();
        if (tasks.isEmpty()) {
            System.out.println("\n📌 No tasks available.");
            return;
        }
        printTaskTable("📋 ALL TASKS", tasks);
    }

    public void showWorkTasks() {
        showTasksByType(WorkTask.class, "💼 WORK TASKS");
    }

    public void showPersonalTasks() {
        showTasksByType(PersonalTask.class, "🏡 PERSONAL TASKS");
    }

    public void showUrgentTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);
        List<Task> urgentTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDueDate().isBefore(next24Hours) && !task.isExpired()) {
                urgentTasks.add(task);
            }
        }

        if (urgentTasks.isEmpty()) {
            System.out.println("\n✅ No urgent tasks found.");
        } else {
            printTaskTable("⏳ URGENT TASKS (DUE IN 24 HOURS)", urgentTasks);
        }
    }

    public void deleteTask(int id) {
        boolean removed = tasks.removeIf(task -> task.getId() == id);
        if (removed) {
            saveTasks();
            System.out.println("\n✅ Task deleted successfully.");
        } else {
            System.out.println("\n❌ Task not found.");
        }
    }

    private void showTasksByType(Class<?> type, String title) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (type.isInstance(task) && !task.isExpired()) {
                filteredTasks.add(task);
            }
        }

        if (filteredTasks.isEmpty()) {
            System.out.println("\n📌 No tasks found.");
        } else {
            printTaskTable(title, filteredTasks);
        }
    }

    private void removeExpiredTasks() {
        tasks.removeIf(Task::isExpired);
        saveTasks();
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                writer.println(task.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private List<Task> loadTasks() {
        List<Task> loadedTasks = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return loadedTasks;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Task task = Task.fromFileString(line);
                    loadedTasks.add(task);
                    scheduleReminder(task);
                } catch (InvalidTaskException e) {
                    System.out.println("Skipping invalid task entry: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }

        return loadedTasks;
    }

    public void startReminderService() {
        Thread reminderThread = new Thread(() -> {
            while (true) {
                try {
                    checkForUpcomingTasks();
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    System.out.println("Reminder service interrupted!");
                }
            }
        });
        reminderThread.setDaemon(true);
        reminderThread.start();
    }

    private void checkForUpcomingTasks() {
        LocalDateTime now = LocalDateTime.now();
        for (Task task : tasks) {
            if (!task.isExpired() && task.getDueDate().isBefore(now.plusMinutes(3))) {
                System.out.println("\n⏳ Reminder: Task '" + task.getDescription() + "' is due soon!");
            }
        }
    }

    private void scheduleReminder(Task task) {
        long delay = task.getDueDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis();
        
        if (delay > 0) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("\n⏳ Reminder: Task '" + task.getDescription() + "' is due now!");
                }
            }, delay);
        }
    }

    private void printTaskTable(String title, List<Task> taskList) {
        System.out.println("\n" + title);
        System.out.println("───────────────────────────────────────────────");
        System.out.printf("| %-3s | %-20s | %-16s | %-8s |\n", "ID", "Description", "Due Date", "Priority");
        System.out.println("───────────────────────────────────────────────");

        for (Task task : taskList) {
            String priorityLabel = switch (task.getPriority()) {
                case 1 -> "🔴 High";
                case 2 -> "🟡 Medium";
                case 3 -> "🟢 Low";
                default -> "Unknown";
            };

            System.out.printf("| %-3d | %-20s | %-16s | %-8s |\n",
                task.getId(),
                task.getDescription(),
                task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                priorityLabel
            );
        }
        System.out.println("───────────────────────────────────────────────");
    }
}
