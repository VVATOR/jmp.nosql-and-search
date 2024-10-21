package com.epam.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfiguration {
    @Bean
    public MongoClient mongoClient(@Value("${user}") String user,
                                   @Value("${password}") String password) {
        return MongoClients.create("mongodb://%s:%s@localhost:27011".formatted(user, password));
    }
}
