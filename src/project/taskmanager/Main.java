package project.taskmanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager manager = new TaskManager();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        while (true) {
            System.out.println("\n══════════════════════════════════════════");
            System.out.println("📌 TASK MANAGER MENU:");
            System.out.println("══════════════════════════════════════════");
            System.out.println("1️⃣ Add a Task");
            System.out.println("2️⃣ Show All Tasks");
            System.out.println("3️⃣ Show Work Tasks");
            System.out.println("4️⃣ Show Personal Tasks");
            System.out.println("5️⃣ Show Urgent Tasks (Due Soon)");
            System.out.println("6️⃣ Delete a Task");
            System.out.println("7️⃣ Exit");
            int option = getIntInput(scanner, "➜ Choose an option: ", 1, 7);

            switch (option) {
                case 1 -> addTask(scanner, manager, formatter);
                case 2 -> manager.showTasks();
                case 3 -> manager.showWorkTasks();
                case 4 -> manager.showPersonalTasks();
                case 5 -> manager.showUrgentTasks();
                case 6 -> {
                    int idToDelete = getIntInput(scanner, "➜ Enter Task ID to delete: ", 1, Integer.MAX_VALUE);
                    manager.deleteTask(idToDelete);
                }
                case 7 -> {
                    System.out.println("👋 Exiting Task Manager...");
                    scanner.close();
                    return;
                }
            }
        }
    }

    // 🔹 **New Method: Add Task**
    private static void addTask(Scanner scanner, TaskManager manager, DateTimeFormatter formatter) {
        System.out.println("\n────────────────────────────────");
        System.out.println("📌 Enter Task Details:");
        System.out.println("────────────────────────────────");

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            System.out.println("❌ Error: Description cannot be empty.");
            return;
        }

        System.out.print("Due Date (yyyy-MM-dd HH:mm): ");
        LocalDateTime dueDate;
        try {
            dueDate = LocalDateTime.parse(scanner.nextLine().trim(), formatter);
            if (dueDate.isBefore(LocalDateTime.now())) {
                System.out.println("❌ Error: Due date must be in the future!");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Invalid date format. Use yyyy-MM-dd HH:mm");
            return;
        }

        int priority = getIntInput(scanner, "Priority (1-High 🔴, 2-Medium 🟡, 3-Low 🟢): ", 1, 3);
        int type = getIntInput(scanner, "Task Type (1-Work 💼, 2-Personal 🏡): ", 1, 2);

        try {
            Task task = (type == 1) ? new WorkTask(description, dueDate, priority) :
                                      new PersonalTask(description, dueDate, priority);
            manager.addTask(task);
            System.out.println("✅ Task added successfully!");
        } catch (InvalidTaskException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // 🔹 **Method for Safe Integer Input**
    private static int getIntInput(Scanner scanner, String message, int min, int max) {
        int value;
        while (true) {
            System.out.print(message);
            try {
                value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("❌ Invalid input! Enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid number.");
            }
        }
    }
}