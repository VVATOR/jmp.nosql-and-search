package com.epam.learn.nosqlandsearch.module1.task1;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DataMigrationRunner {
    public static void main(String[] args) {
        String sqlUrl = "jdbc:postgresql://localhost:5432/ticket_booking";
        String sqlUser = "postgres";
        String sqlPassword = "pass";

        try (Connection sqlConnection = DriverManager.getConnection(sqlUrl, sqlUser, sqlPassword);
//             var mongoClient = MongoClients.create("mongodb://root:password@localhost:27017")) {
//             var mongoClient = MongoClients.create("mongodb://root:password@mongo1")) {
//             var mongoClient = MongoClients.create("mongodb://mongo1/?directConnection=true")) { // ok works
             var mongoClient = MongoClients.create("mongodb://mongo1/?directConnection=true")) { // ok works

            MongoDatabase mongoDatabase = mongoClient.getDatabase("ticket_booking");
            MongoCollection<Document> eventsCollection = mongoDatabase.getCollection("events");

            String query = """
                   SELECT e.id, e.title, e.date, e.ticket_price, u.name, u.email, t.place, t.category
                   FROM events e
                   JOIN tickets t ON e.id = t.event_id
                   JOIN users u ON t.user_id = u.id
                   ORDER BY e.id ASC
                   """;

            try (Statement stmt = sqlConnection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                long currentEventId = -1;
                Document eventDoc = null;
                List<Document> tickets = new ArrayList<>();

                while (rs.next()) {
                    long eventId = rs.getLong("id");
                    String title = rs.getString("title");
                    Timestamp date = rs.getTimestamp("date");
                    double ticketPrice = rs.getDouble("ticket_price");

                    String userName = rs.getString("name");
                    String userEmail = rs.getString("email");
                    int place = rs.getInt("place");
                    String category = rs.getString("category");

                    if (currentEventId != eventId) {
                        if (eventDoc != null) {
                            eventDoc.append("tickets", tickets);
                            eventsCollection.insertOne(eventDoc);
                        }
                        tickets = new ArrayList<>();
                        eventDoc = new Document("_id", eventId)
                                .append("title", title)
                                .append("date", date.toInstant().toString())
                                .append("ticket_price", ticketPrice);
                        currentEventId = eventId;
                    }

                    Document ticket = new Document("user", new Document("name", userName)
                            .append("email", userEmail))
                            .append("place", place)
                            .append("category", category);
                    tickets.add(ticket);
                }

                if (eventDoc != null) {
                    eventDoc.append("tickets", tickets);
                    eventsCollection.insertOne(eventDoc);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}