package com.epam.controler;

import com.epam.model.TaskModel;
import com.epam.repo.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void displayAllTasks() {
        logger.info("Displaying all tasks:");
        taskRepository.findAll().forEach(System.out::println);
    }

    public void displayOverdueTasks() {
        logger.info("Displaying overdue tasks");
        taskRepository.findAll().forEach(System.out::println);
    }

    public void addTask(TaskModel task) {
        final var saved = taskRepository.save(task);
        logger.info("Task {} added successfully!", saved.getId());
    }

    public void deleteTask(String taskId) {
        taskRepository.deleteById(taskId);
        logger.info("Task {} deleted!", taskId);
    }

    public void searchByDescription(String description) {
        logger.info("Search by description:");
        taskRepository.findAllByDescription(description).forEach(System.out::println);
    }

    public void displayTasksByCategory(String category) {
        logger.info("Tasks from category '{}':", category);
        taskRepository.findAllByCategory(category).forEach(System.out::println);
    }

    public void displaySubtasksByCategory(String subtasksubtasksName) {
        logger.info("Subtasks from category '{}':", subtasksubtasksName);
//        taskRepository.findAllBySubtasksName(subtasksubtasksName).forEach(System.out::println);
    }




        public static boolean isPalindrome(String str) {
            // code
            String normalizedStr= str.replaceAll("[^a-zA-z0-9]","").toLowerCase();
            int length= normalizedStr.length();
            for(int i=0;i<length/2;i++) {
                if(normalizedStr.charAt(i)!=normalizedStr.charAt(length-i-1)) {
                    return false;
                }
            }
            return true;

        }

        public static void main(String[] args) {
            final String str = "A man, a plan, a canal, Panama!";
            boolean result = isPalindrome(str);
            System.out.println("Is the string a palindrome? " + result);
        }


    public void updateTaskModel(TaskModel taskForUpdate) {
        final var updatedTask = taskRepository.save(taskForUpdate);
        logger.info("Task {} updated successfully!", updatedTask.getId());
    }
}
