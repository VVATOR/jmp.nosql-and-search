# jmp.nosql-and-search

## Module 1: NoSQL 


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
