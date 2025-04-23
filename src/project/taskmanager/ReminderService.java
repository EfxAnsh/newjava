package project.taskmanager;

import java.util.List;

public class ReminderService implements Runnable {
    private List<Task> tasks;
    private boolean running = true;

    public ReminderService(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(60000); 
                checkDeadlines();
            } catch (InterruptedException e) {
                System.out.println("Reminder Service Interrupted.");
                break;
            }
        }
    }

    private void checkDeadlines() {
        for (Task task : tasks) {
            if (task.getDueDate().isBefore(java.time.LocalDateTime.now().plusMinutes(10))) {
                task.remind();
            }
        }
    }

    public void stop() {
        running = false;
    }
}