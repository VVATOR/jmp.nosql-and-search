package com.epam.learn.elasticsearch.task4.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.epam.learn.elasticsearch.task4.models.Employee;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmployeeService {

    public static final String EMPLOYEES_INDEX_NAME = "employees";
    private final ElasticsearchClient client;

    public EmployeeService(ElasticsearchClient client) {
        this.client = client;
    }

    public List<Employee> getAllEmployees() throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s.index(EMPLOYEES_INDEX_NAME));
        SearchResponse<Employee> searchResponse = client.search(searchRequest, Employee.class);

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    public Employee getEmployeeById(String id) throws IOException {
        GetResponse<Employee> response = client.get(g -> g
                .index(EMPLOYEES_INDEX_NAME)
                .id(id), Employee.class);

        return response.source();
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
        Query query = MatchQuery.of(m -> m
                .field(field)
                .query(value)
        )._toQuery();

        SearchResponse<Employee> response = client.search(s -> s
                .index(EMPLOYEES_INDEX_NAME)
                .query(query), Employee.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    public double aggregateEmployeesByField(String field, String metricType) throws IOException {
        Aggregation aggregation = switch (metricType) {
            case "avg" -> AggregationBuilders.avg(a -> a.field(field));
            case "sum" -> AggregationBuilders.sum(s -> s.field(field));
            case "min" -> AggregationBuilders.min(m -> m.field(field));
            case "max" -> AggregationBuilders.max(m -> m.field(field));
            default -> throw new IllegalArgumentException("Unsupported metric type: " + metricType);
        };
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(EMPLOYEES_INDEX_NAME)
                .aggregations("agg", aggregation)
        );
        SearchResponse<Void> response = client.search(searchRequest, Void.class);
        Aggregate aggregate = response.aggregations().get("agg");
        return aggregate.avg().value();
    }

}
