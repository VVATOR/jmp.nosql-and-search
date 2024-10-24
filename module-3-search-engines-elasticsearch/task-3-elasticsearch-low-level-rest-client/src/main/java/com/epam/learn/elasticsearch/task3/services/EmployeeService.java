package com.epam.learn.elasticsearch.task3.services;

import com.epam.learn.elasticsearch.task3.models.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    public static final String EMPLOYEES_INDEX_NAME = "employees";
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Value("${elastic-search.host}")
    private String elasticSearchHost = "http://localhost:9200/";

    public EmployeeService() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    public List<Employee> getAllEmployees() throws IOException {
        HttpGet request = new HttpGet(elasticSearchHost + EMPLOYEES_INDEX_NAME + "/_search");
        HttpResponse response = httpClient.execute(request);
        String json = EntityUtils.toString(response.getEntity());
        Map<String, Object> responseMap = objectMapper.readValue(json, Map.class);
        List<Map<String, Object>> hits = (List<Map<String, Object>>) ((Map<String, Object>) responseMap.get("hits")).get("hits");

        // Преобразуем каждый элемент hits в объект Employee и добавляем id из _id
        return hits.stream()
                .map(hit -> {
                    Employee employee = objectMapper.convertValue(hit.get("_source"), Employee.class);
                    employee.setId((String) hit.get("_id"));
                    return employee;
                })
                .toList();
    }

    public Employee getEmployeeById(String id) throws IOException {
        HttpGet request = new HttpGet(elasticSearchHost + EMPLOYEES_INDEX_NAME + "/_doc/" + id);
        HttpResponse response = httpClient.execute(request);
        String json = EntityUtils.toString(response.getEntity());
        Map<String, Object> responseMap = objectMapper.readValue(json, Map.class);
        Employee employee = objectMapper.convertValue(responseMap.get("_source"), Employee.class);
        employee.setId((String) responseMap.get("_id"));
        return employee;
    }


    public void createEmployee(String id, Employee employee) throws IOException {
        String json = objectMapper.writeValueAsString(employee);
        HttpPut request = new HttpPut(elasticSearchHost + EMPLOYEES_INDEX_NAME + "/_doc/" + id);
        request.setEntity(new StringEntity(json));
        request.setHeader("Content-Type", "application/json");

        httpClient.execute(request);
    }

    public void deleteEmployee(String id) throws IOException {
        HttpDelete request = new HttpDelete(elasticSearchHost + EMPLOYEES_INDEX_NAME + "/_doc/" + id);
        httpClient.execute(request);
    }

    public List<Employee> searchEmployees(String field, String value) throws IOException {
        String query = String.format("{\"query\": {\"match\": {\"%s\": \"%s\"}}}", field, value);
        HttpPost request = new HttpPost(elasticSearchHost + EMPLOYEES_INDEX_NAME + "/_search");
        request.setEntity(new StringEntity(query));
        request.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(request);
        String json = EntityUtils.toString(response.getEntity());
        Map<String, Object> responseMap = objectMapper.readValue(json, Map.class);
        List<Map<String, Object>> hits = (List<Map<String, Object>>) ((Map<String, Object>) responseMap.get("hits")).get("hits");
        return hits.stream()
                .map(hit -> objectMapper.convertValue(hit.get("_source"), Employee.class))
                .toList();
    }


    public double aggregateEmployeesByField(String field) throws IOException {
        String aggregationQuery = String.format(
                "{ \"size\": 0, \"aggs\": { \"agg\": { \"%s\": { \"field\": \"%s\" } } } }",
                "avg", field
        );

        HttpPost request = new HttpPost(elasticSearchHost + EMPLOYEES_INDEX_NAME + "/_search");
        request.setEntity(new StringEntity(aggregationQuery));
        request.setHeader("Content-Type", "application/json");

        HttpResponse response = httpClient.execute(request);
        String json = EntityUtils.toString(response.getEntity());

        Map<String, Object> responseMap = objectMapper.readValue(json, Map.class);
        Map<String, Object> aggregations = (Map<String, Object>) responseMap.get("aggregations");
        Map<String, Object> agg = (Map<String, Object>) aggregations.get("agg");

        return (Double) agg.get("value");
    }
}
