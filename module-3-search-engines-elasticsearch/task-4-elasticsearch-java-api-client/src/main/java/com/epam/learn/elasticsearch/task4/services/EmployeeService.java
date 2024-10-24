package com.epam.learn.elasticsearch.task4.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.epam.learn.elasticsearch.task4.models.Employee;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    public static final String EMPLOYEES_INDEX_NAME = "employees";
    private final ElasticsearchClient client;

    public EmployeeService(ElasticsearchClient client) {
        this.client = client;
    }

    public List<Employee> getAllEmployees() throws IOException {
        SearchResponse<Employee> searchResponse = client.search(s -> s
                .index(EMPLOYEES_INDEX_NAME)
                .query(q -> q.matchAll(m -> m)), Employee.class);

        return searchResponse.hits().hits().stream().map(hit -> {
            Employee employee = hit.source();
            employee.setId(hit.id());
            return employee;
        }).collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) throws IOException {
        SearchResponse<Employee> searchResponse = client.search(s -> s
                        .index(EMPLOYEES_INDEX_NAME)
                        .query(q -> q
                                .term(t -> t
                                        .field("_id")
                                        .value(id)
                                )
                        ),
                Employee.class
        );

        if (searchResponse.hits().hits().isEmpty()) {
            return null;
        }

        Hit<Employee> hit = searchResponse.hits().hits().get(0);
        Employee employee = hit.source();

        employee.setId(hit.id());

        return employee;
    }

    public void createEmployee(String id, Employee employee) throws IOException {
        client.index(i -> i
                .index(EMPLOYEES_INDEX_NAME)
                .id(id)
                .document(employee));
    }

    public void deleteEmployee(String id) throws IOException {
        client.delete(d -> d
                .index(EMPLOYEES_INDEX_NAME)
                .id(id));
    }

    public List<Employee> searchEmployees(String field, String value) throws IOException {
        SearchResponse<Employee> searchResponse = client.search(s -> s
                .index(EMPLOYEES_INDEX_NAME)
                .query(q -> q
                        .match(t -> t
                                .field(field)
                                .query(value)
                        )
                ), Employee.class);

        return searchResponse.hits().hits().stream().map(hit -> {
            Employee employee = hit.source();
            employee.setId(hit.id());
            return employee;
        }).toList();
    }

    public double aggregateEmployeesByField(String field) throws IOException {
        Aggregation aggregation = AggregationBuilders.avg(a -> a.field(field));
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(EMPLOYEES_INDEX_NAME)
                .aggregations("agg", aggregation)
        );
        SearchResponse<Void> response = client.search(searchRequest, Void.class);
        Aggregate aggregate = response.aggregations().get("agg");
        return aggregate.avg().value();
    }

}
