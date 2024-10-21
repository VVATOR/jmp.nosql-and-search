package com.epam.repo;

import com.epam.model.TaskModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<TaskModel, String> {
    Iterable<TaskModel> findAllByDescription(String description);

    Iterable<TaskModel> findAllByCategory(String category);

//    Iterable<TaskModel> findAllBySubtasksName(String name);
}
