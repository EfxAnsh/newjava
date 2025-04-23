package project.taskmanager;

import java.time.LocalDateTime;

public class PersonalTask extends Task {
    public PersonalTask(String description, LocalDateTime dueDate, int priority) throws InvalidTaskException {
        super(description, dueDate, priority);
    }

    @Override
    public void sendReminder() {
        System.out.println("🔔 Personal Task Reminder: \"" + getDescription() + "\" is due at " + getDueDate());
    }
}