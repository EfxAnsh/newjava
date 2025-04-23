package project.taskmanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Task {
    private static int idCounter = 1;  
    private int id;
    private String description;
    private LocalDateTime dueDate;
    private int priority;

    public Task(String description, LocalDateTime dueDate, int priority) throws InvalidTaskException {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidTaskException("Description cannot be empty.");
        }
        if (dueDate.isBefore(LocalDateTime.now())) {    
            throw new InvalidTaskException("Due date must be in the future.");
        }

        this.id = idCounter++;  
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(dueDate);
    }

    public abstract void sendReminder();

    public void remind() {
        if (LocalDateTime.now().isAfter(dueDate.minusMinutes(10))) { // 🔔 10-minute reminder
            sendReminder();
        }
    }

    public String toFileString() {
        return id + "|" + description + "|" + dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "|" + priority + "|" + getClass().getSimpleName();
    }

    public static Task fromFileString(String line) throws InvalidTaskException {
        String[] parts = line.split("\\|");
        int savedId = Integer.parseInt(parts[0]);  
        String description = parts[1];
        LocalDateTime dueDate = LocalDateTime.parse(parts[2], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        int priority = Integer.parseInt(parts[3]);
        String type = parts[4];

        Task task;
        if (type.equals("WorkTask")) {
            task = new WorkTask(description, dueDate, priority);
        } else if (type.equals("PersonalTask")) {
            task = new PersonalTask(description, dueDate, priority);
        } else {
            throw new InvalidTaskException("Unknown task type.");
        }

        task.id = savedId;
        if (savedId >= idCounter) {
            idCounter = savedId + 1;
        }

        return task;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "ID: " + id + " | " + description + " | Due: " + dueDate.format(formatter) + " | Priority: " + getPriorityLabel();
    }

    private String getPriorityLabel() {
        return switch (priority) {
            case 1 -> "🔴 High";
            case 2 -> "🟡 Medium";
            case 3 -> "🟢 Low";
            default -> "Unknown";
        };
    }
}