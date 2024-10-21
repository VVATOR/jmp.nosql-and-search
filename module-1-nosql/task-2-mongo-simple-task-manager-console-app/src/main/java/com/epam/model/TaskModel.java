package com.epam.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Document
public class TaskModel {
    private String id;
    private Date dateOfCreation;
    private Date deadline;
    private String name;
    private String description;
    private List<SubtaskModel> subtaskModels;
    private String category;
}
