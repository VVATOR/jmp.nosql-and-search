# jmp.nosql-and-search

# Module 3: Search Engines: Elasticsearch

## Elasticsearch Practical Tasks

## Task 1 - Indexing and CRUD operations (20 Points)


##### 1.	Start Elasticsearch
    Run bin/elasticsearch (or bin\elasticsearch.bat on Windows) 
    You can go to Elasticsearch Head (Chrome extention) to observe your cluster
    It runs on http://localhost:9200
    Check Elasticsearch health   GET http://localhost:9200/_cluster/health
    and Cluster stats            GET http://localhost:9200/_cluster/stats 
##### 2.	Create an index and populate it with data
    Download file employees.jsos
    It's contains a fully prepared data for bulk loading to Elasticsearch. 
    Create your index named "employees". 
    For that in Postman run  PUT http://localhost:9200/employees/_bulk
    with "binary" body uploading the "employees.json" file.


##### 3. Perform CRUD operations using Postman, Elasticsearch Head or other Rest client.
##### [ NOTE !!! ] For this and all the following tasks make screenshots of your requests/responses and attach them.

[Document API](https://www.elastic.co/guide/en/elasticsearch/reference/8.6/docs.html)\
[Bulk API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html)\
[Keyword vs Text](https://codecurated.com/blog/elasticsearch-text-vs-keyword/)    
[Search API](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html)

    1. Get all records  (you may starts with POST http://localhost:9200/employees/_search to see ids assigned to the records. 
       It was possible to assign ids explicitely while doing bulk upload, but we let Elasticsearch to set ids randomly).

    2. Get an employee by id.

    3. Create a new employee and put in into the index. Let it be

```json
{
  "name": "Ana Brown",
  "dob": "1993-03-19",
  "address": {
    "country": "Belarus",
    "town": "Gomel"
  },
  "email": "anabrown9@gmail.com",
  "skills": [
    "Java",
    "AWS"
  ],
  "experience": 10,
  "rating": 9.2,
  "description": "confident, ambitious, highly motivated Java experience interview learning python",
  "verified": true,
  "salary": 30000
}
```
       Specify id in the url. Query the new employee by its id.
    
    4. Add one more employee and try multi-get request. It may look like: GET http://localhost:9200/employees/_mget {"docs": [{"_id": "1"},{"_id": "2"}]}
    
    5. Learn mapping, automatically created by Elasticsearch. 
       GET http://localhost:9200/employees/_mapping
       Pay attention to string values types. They are both "text" and "keyword". Learn about the difference.

    6. Update one of empoyee data. Add one more skill.
    
    7. Delete one of employees by its id.

## Task 2 - Querying and aggregating data (40 Points)

[Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)\
[Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)\
[Metrics aggregation](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics.html)\
[Joining quieries](https://www.elastic.co/guide/en/elasticsearch/reference/master/joining-queries.html)\
[Suggesters](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters.html)

##### 1. Searching

     1. Get employees with  "description":"motivated" using Match query.
 
     2. Get employees with  "description":"hightly motivated" using Match query. 
        Do you still get Carey Catlett in the response? He has only "motivated" in his description. 
        It's because by default Elastic uses OR condition between the two words in Match condition. 
        Add "operator": AND to the condition and see how it works now. You need to restructure Match condition a little bit.
     
     3. Run multi_match query to get employees with "python" either in "skills" or in "description" fields.

     4. Get employee with "name" : "stepanie" using Term query. 
        Try the same with "name" : "Stepanie" and  "name" : "Stepanie Spain". Did you get the result? Why?

     5. Get employee with "name.keyword" : "stepanie" using Terms query.
        Try "name.keyword" : "Stepanie Spain". Review the difference between Match and Term queries as well as between text and keyword types search.
     
     6. Get employees by several names using Terms query with array of values. 
      
     7. Get employees with experience from 5 to 20 years using Range query.

     8. Get employees younger then 1,Jan,2000 using Range query.
     
     9. Perform Wildcard search, for example "address.town":{"value": "b*y"}.

     10. Replace it by Regexp to get only "Batley".

     11. Investigate how Fuzzy query works. Make two spelling mistakes in the town, set "fuzziness": "2",  and check you get the town in the response anyway. 

##### 2. Aggregating

     1. Aggregate employees by skills. You shoud use "skills.keyword" here.
      
     2. Filter out employees by adding filter condition to the previous aggregation query.
        Aggregate verified employees only.

     3. Add average metric on "field": "rating" to every bucket from the previous aggregation. 
        You'll need to add nested avg aggregation.

     4. Replace avarage aggregation by stats aggregation. Sort skills buckets by average stats metric DESC.
        "order": {"rating_stats.avg": "desc"}. 