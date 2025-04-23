package project.taskmanager;

import java.time.LocalDateTime;

public class WorkTask extends Task {
    public WorkTask(String description, LocalDateTime dueDate, int priority) throws InvalidTaskException {
        super(description, dueDate, priority);
    }

    @Override
    public void sendReminder() {
        System.out.println("🚨 Work Task Reminder: \"" + getDescription() + "\" is due at " + getDueDate());
    }
}