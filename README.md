**Capstone Project Report**

**1. Problem Statement**
In today’s fast-paced environment, students and professionals often struggle to manage and prioritize their daily tasks effectively. Existing solutions either require a constant internet connection or are too complex for basic usage. There is a need for a simple, offline desktop-based personal task manager that allows users to add, prioritize, and get reminders for tasks.

**2. Introduction & Project Basics**
The "Personal Task Manager with Prioritization and Reminders" is a Java-based desktop application aimed at helping users organize their tasks into categories such as personal and work. It allows setting deadlines, assigning priorities, and receives reminder notifications. This standalone application uses object-oriented programming principles and a clean JavaFX graphical user interface.

**3. Literature Review / Similar Work**
Several task management applications such as Todoist, Microsoft To Do, and Google Tasks offer robust task organization features. However, most of these require internet access and involve cloud syncing. This project offers a lightweight, offline alternative with fundamental features including deadline tracking and reminders, making it ideal for users seeking simplicity and privacy.

**4. UML & Database Design Diagrams**
- **Class Diagram:**
  - `Task` (abstract)
    - Fields: name, deadline, priority
    - Methods: getDetails()
  - `WorkTask` extends `Task`
  - `PersonalTask` extends `Task`
  - `TaskManager`
    - Fields: List<Task> tasks
    - Methods: addTask(), removeTask(), getAllTasks(), checkReminders()
  - `ReminderService`
    - Method: checkDeadlines()
  - `TaskManagerGUI` (extends JavaFX Application)

- **Data Storage Design:**
  - Tasks are stored in a simple text file to ensure persistence between sessions.
  - Each task record includes: name, deadline, priority, and type.

**5. Graphical User Interface (GUI)**
The application uses JavaFX for its GUI. It includes:
- TextField inputs for task name and deadline
- ComboBox for task type (Work/Personal)
- Button to add task
- ListView to display tasks
- Alerts to show reminders

**6. Java Code Demonstration (with OOPs concepts)**
- **Encapsulation:** Each task type is encapsulated in its own class.
- **Inheritance:** `WorkTask` and `PersonalTask` inherit from the `Task` abstract class.
- **Polymorphism:** Tasks are handled via their common superclass `Task`, allowing generalized handling.
- **Abstraction:** The reminder checking is abstracted in a separate service class (`ReminderService`).

**7. Results / Outputs**
- Users can successfully add and view tasks in the GUI.
- Tasks persist between sessions using file-based storage.
- The application checks every 60 seconds for upcoming deadlines and shows reminder pop-ups.
- Users can filter and categorize tasks based on type and priority.

**8. Conclusion & Key Points**
The Personal Task Manager project achieves its goal of offering a simple, effective offline solution for task management. With a user-friendly interface, persistent data storage, and reminder functionality, it fulfills the essential needs of a basic productivity tool while demonstrating solid object-oriented design and JavaFX GUI integration.

**Key Points:**
- JavaFX GUI and clean UI
- Persistent task storage using text files
- Real-time reminders using threads
- Good implementation of OOP concepts

**Next Steps / Improvements:**
- Add task editing and deletion features
- Enable sorting and filtering by priority or deadline
- Introduce notifications using desktop alerts or sounds
- Support recurring tasks














## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
