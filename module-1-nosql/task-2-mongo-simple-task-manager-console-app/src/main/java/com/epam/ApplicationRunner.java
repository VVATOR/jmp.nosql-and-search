package com.epam;

import com.epam.views.ConsoleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationRunner implements CommandLineRunner {
    private final ConsoleManager consoleManager;

    @Autowired
    public ApplicationRunner(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        consoleManager.menu();
    }
}
