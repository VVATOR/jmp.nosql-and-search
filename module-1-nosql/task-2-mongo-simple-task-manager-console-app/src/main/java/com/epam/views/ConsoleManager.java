package com.epam.views;


import com.epam.model.SubtaskModel;
import com.epam.constants.ProjectConstants;
import com.epam.controler.TaskController;
import com.epam.model.TaskModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


@Component
public class ConsoleManager {
    private final Logger logger = LoggerFactory.getLogger(ConsoleManager.class);
    private final TaskController taskController;

    @Autowired
    public ConsoleManager(TaskController taskController) {
        this.taskController = taskController;
    }

    public void menu() {
        try (final var scanner = new Scanner(System.in);) {
            boolean exit = false;
            while (!exit) {
                logger.info("--- Task Manager ---");
                logger.info("1. Display all tasks");
                logger.info("2. Display overdue tasks");
                logger.info("3. Display tasks by category");
                logger.info("4. Display subtasks by category");
                logger.info("5. Add new task");
                logger.info("6. Update task");
                logger.info("7. Delete task");
                logger.info("8. Search by description");
                logger.info("9. Search by subtask name");
                logger.info("10. Exit");
                logger.info("Select an option: ");
                int option = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (option) {
                    case 1:
                        taskController.displayAllTasks();
                        break;
                    case 2:
                        taskController.displayOverdueTasks();
                        break;
                    case 3:
                        logger.info("Enter the task category: ");
                        String category = scanner.nextLine();
                        taskController.displayTasksByCategory(category);
                        break;
                    case 4:
                        logger.info("Enter the subtask category: ");
                        String subtaskCategory = scanner.nextLine();
                        taskController.displaySubtasksByCategory(subtaskCategory);
                        break;
                    case 5:
                        final var task = getTaskModel(scanner);
                        taskController.addTask(task);
                        break;
                    case 6:
                        final var taskForUpdate = updateTaskModel(scanner);
                        taskController.updateTaskModel(taskForUpdate);
                        break;
                    case 7:
                        logger.info("Enter the task ID to delete: ");
                        String taskId = scanner.nextLine();
                        taskController.deleteTask(taskId);
                        break;
                    case 8:
                        logger.info("Enter the task Description: ");
                        String description = scanner.nextLine();
                        taskController.searchByDescription(description);
//                        taskController.searchByDescription(scanner);
                        break;
                    case 9:
//                        taskController.searchBySubtaskName(scanner);
                        break;
                    case 10:
                        exit = true;
                        break;
                    default:
                        logger.error("Invalid option. Try again.");
                }
            }
            scanner.close();
        }
//        MongoDBConnection.close();
    }

    private TaskModel updateTaskModel(Scanner scanner) {
        logger.info("Enter task id: ");
        final var taskId = scanner.nextLine();
        logger.info("Enter task name: ");
        final var name = scanner.nextLine();
        logger.info("Enter task description: ");
        final var description = scanner.nextLine();
        logger.info("Enter task category: ");
        final var category = scanner.nextLine();
        logger.info("Enter deadline (yyyy-MM-dd): ");
        final var deadlineStr = scanner.nextLine();
        final var deadline = parseDate(deadlineStr);
        List<SubtaskModel> subtaskModels = new ArrayList<>();
        String addMore;
        do {
            logger.info("Do you want to add a subtask? (yes/no): ");
            addMore = scanner.nextLine();
            if ("yes".equalsIgnoreCase(addMore)) {
                logger.info("Enter subtask name: ");
                final var subtaskName = scanner.nextLine();
                logger.info("Enter subtask description: ");
                final var subtaskDescription = scanner.nextLine();
                final var subtask = new SubtaskModel(subtaskName, subtaskDescription);
                subtaskModels.add(subtask);
            }
        } while ("yes".equalsIgnoreCase(addMore));

        return new TaskModel(taskId, new Date(), deadline, name, description, subtaskModels, category);
    }

    private TaskModel getTaskModel(Scanner scanner) {
        logger.info("Enter task name: ");
        final var name = scanner.nextLine();

        logger.info("Enter task description: ");
        final var description = scanner.nextLine();

        logger.info("Enter task category: ");
        final var category = scanner.nextLine();

        logger.info("Enter deadline (yyyy-MM-dd): ");
        final var deadlineStr = scanner.nextLine();


        Date deadline = parseDate(deadlineStr);

        List<SubtaskModel> subtaskModels = new ArrayList<>();
        String addMore;
        do {
            logger.info("Do you want to add a subtask? (yes/no): ");
            addMore = scanner.nextLine();
            if ("yes".equalsIgnoreCase(addMore)) {
                logger.info("Enter subtask name: ");
                final var subtaskName = scanner.nextLine();

                logger.info("Enter subtask description: ");
                final var subtaskDescription = scanner.nextLine();

                final var subtask = new SubtaskModel(subtaskName, subtaskDescription);
                subtaskModels.add(subtask);
            }
        } while ("yes".equalsIgnoreCase(addMore));

        return new TaskModel(null, new Date(), deadline, name, description, subtaskModels, category);
    }

    public Date parseDate(String dateStr) {
        final var format = new SimpleDateFormat(ProjectConstants.YYYY_MM_DD_DATE_FORMAT);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            logger.error("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }
}