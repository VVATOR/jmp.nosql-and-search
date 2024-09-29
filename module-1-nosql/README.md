# jmp.nosql-and-search

## Module 1: NoSQL 

> **💡 NOTE**: all tasks should be implemented with the newest versions of MongoDB (`>= 6.0`), Cassandra (`>= 4.1.0`) and
Couchbase (`>= 7.1`).

Task 1: Backend Database Migration
===================================

**Cost**: 50 points.

Move one of your pet applications/mentoring projects with DAO and simple CRUD to MongoDB.
In case you don't have such projects you can use the sample project attached ([ticket-booking.zip](task-1-backend-db-migration/static/ticket-booking.zip))

Install MongoDB and use the corresponding Java driver.

* Define the data model for MongoDB (direct table-collection mapping is not the best idea).
* Write data migration job (via SQL and MongoDriver operations).
* Use an aggregation mechanism to get grouped results from the database.
* (Optional) Run this job in one transaction for the SQL database and in one multi-document transaction for MongoDB.
* (Optional) Use appropriate indices to improve query performance.
* (Optional) Make a replica set with 1 primary and 2 secondary nodes, and execute your tests/client code over the MongoDB cluster.

Task 2: Choose one (either MongoDB, Cassandra, or Couchbase) database and complete the appropriate tasks
===================================

MongoDB
===================================

Install MongoDB and use corresponding Java driver.

Create simple task manager console app. Your tasks should have the following fields:

* date of creation;
* deadline;
* name;
* description;
* list of subtasks with simple structure (name/description);
* category.

Provide the following operations:

1. Display on console all tasks.
2. Display overdue tasks.
3. Display all tasks with a specific category (query parameter).
4. Display all subtasks related to tasks with a specific category (query parameter).
5. Perform insert/update/delete of the task.
6. Perform insert/update/delete all subtasks of a given task (query parameter).
7. Support full-text search by word in the task description.
8. Support full-text search by a sub-task name.

For the highest score, you can try to implement DAO with any ORM solution for MongoDB (+10 bonus points).

Social Network with MongoDB (Optional)
==================================

Write a simple app with a few classes reflecting typical Social Network (Users, Messages, Movies, Audio tracks,
Friendships etc.).
Keep more than `10^9` instances for each class into MongoDB database using MongoDB driver.

Provide simple reporting based on MongoDB Aggregation Framework:

1. Average number of messages by day of week.
2. Max number of new friendships from month to month.
3. Min number of watched movies by users with more than `100` friends.

Play with different data models, do not fear to experiment with denormalization.

Optionally use appropriate indices to improve query performance.

Optionally make a replica-set with 1 primary and 2 secondary nodes, execute your tests/client code
over the MongoDB cluster.


The Cassandra Ring
===========================

1. Install Cassandra and use corresponding Java driver.
2. Build Cassandra cluster with 4 nodes.
3. Balance the cluster and distribute keys.
4. Provides screenshots of a running cluster and filled tables.
5. Write a performance test app which stores logs (logs can be tracked events with different structure) from your
   working app into Cassandra Cluster.
6. Measure different useful statistics for log writing (throughput - avg, 95th percentile and so on).
7. Provide simple admin operations (to edit logs) via Cassandra Java driver.
8. Use CQL to make queries for generating reports.
9. List of desired reports and collected metrics should be provided by your mentor.

Social Network with Cassandra (Optional)
======================================

Take the model and data from the previous task and implement backend with Cassandra (build separate tables for each kind
of report).
Could we use Spring solution to reduce copy-paste and light-switching of NoSQL database from MongoDB to Cassandra?

Discuss with mentor, what solution is better for this data-case?

Optional sub-tasks:

* use appropriate indices to improve query performance;
* build Cassandra cluster with 4 nodes;
* balance the cluster and distribute keys;
* tune commitlog/memtable properties to reach better performance of your insert or select operations;
* measure performance of a query execution and prepare a simple performance report.

Read [this](https://cassandra.apache.org/doc/latest/cassandra/configuration/) to configure Cassandara.

Couchbase
======================================

### Prerequisites

1. Follow [this](https://docs.couchbase.com/server/current/install/install-intro.html) process to install Couchbase
2. Java version 8+
3. Maven
4. Java IDE

### Task 1: Connect Spring Boot with Couchbase and create first document (10 points)

1. Create a spring-boot maven project with at least the following dependencies:

```
spring-boot-starter-data-couchbase
spring-boot-starter-web
```

2. Create a Java Representation of User with fields:

```json
{
  "id": "9e9a5147-8ebb-4344-a55a-b845aa6e2697",
  "email": "john_doe@epam.com",
  "fullName": "John Doe",
  "birthDate": "1990-10-10",
  "gender": "MALE"
}
```

3. Create a repository for User with ability to
    * create a new user
    * find user by Id
4. Implement a service layer
5. Implement API layer with the following REST endpoints:
    * ```GET /api/v1/user/{id}``` - to get a user by id
    * ```POST /api/v1/user``` - to create a user
6. Create a new user via API method call
7. Verify that the created user is accessible via API method call

### Task 2: Create index for searching by field (10 points)

1. Navigate to Couchbase Web Console, to Query tab and execute a query to
   create an index for email field.
2. Add methods to User repository and service with ability to retrieve a user by email
3. Add the following API endpoint:
    * ```GET /api/v1/user/email/{email}``` - to get a user by email
4. Verify that the user is available by email via API method call (do not forget about index)

### Task 3: Embedded objects (10 points)

1. Create a Java Representation of Sport with the following fields:

```json
{
  "id": "9e9a5147-8ebb-4344-a55a-b845aa6e2697",
  "sportName": "Tennis",
  "sportProficiency": "ADVANCED"
}
```

2. Add Sports to the User model
3. Create a method in UserService with ability to add sport to user
4. Implement the following API endpoint to update user with new sport
    * ```PUT /api/v1/user/{id}/sport```
5. Verify that the Couchbase document is updated after the API method call

### Task 4: Search by embedded object fields (10 points)

1. Create a method in UserService with the ability to search users by sportName
2. Add a method for UserRepository with custom query to retrieve users by sportName
3. Do not forget to create an index in Couchbase
4. Implement the following API endpoint to retrieve users by sport's name:
    * ```GET /api/v1/user/sport/{sportName}```
5. Verify that some users are returned after the method call

### Task 5: Perform full-text search (10 points)

1. Create a search index via Couchbase Web Console
2. Perform a search request via Couchbase Web Console and make sure that you are able to see some
   results
3. Create a Repository for searching user by query
4. Implement a service layer to return users by a search query
5. Create a SearchApi with the following API method:
    * ```GET /api/v1/search/user?q=``` - to search users by query
6. Verify that some users are returned after the method call     