# jmp.nosql-and-search

## Module 2: Cache and Redis 

# Homework

## Prerequisites
1. Read about [Rate limiting](https://en.wikipedia.org/wiki/Rate_limiting)
2. Read about [HTTP 429 response code](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429)
3. Read about rate limiting algorithms - [Rate limiting algorithms](https://betterprogramming.pub/4-rate-limit-algorithms-every-developer-should-know-7472cb482f48)
4. Read about [Redis key expiration](https://redis.io/commands/expire/).
5. Read about Bucket4J [How to Create a Rate Limiter using Bucket4J and Redis](https://www.freecodecamp.org/news/rate-limiting-with-bucket4j-and-redis/)

## Problem definition
Your goal is to design and implement simple Rate Limit Server using Redis Cluster.
When client make any request to some business service, business service send rate limit request to Rate limit server over HTTP.
If server respond with 200 OK, business service process this request. If client exceed it requests limit, rate limit server
respond with 429 Too many requests and business service respond back to client with HTTP 429 without processing the request.

![img.png](./statics/Redis-Lab-Context-Diagram.drawio.png)

![img.png](./statics/Redis-Lab-Behavior-And-Responsibilities.drawio.png)
Rate limit request consist of array of request descriptors. Request descriptor consists of following fields:
* accountId - string, optional
* clientIp - string, optional
* requestType - string, optional

Request descriptor should contain at least one field.

Example of request:

```json
[
  {
    "clientIp": "127.0.0.1",
    "accountId": "1",
    "requestType": "login"
  },
  {
    "clientIp": "127.0.0.1"
  }
]
```

Rate limit rules are configured statically in ratelimitRules.yaml resource and loaded during the application startup.
Rate limit rule consists of following fields:
* accountId - string, optional, can be empty
* clientIp - string, optional, can be empty
* requestType - string, optional, can't be empty
* allowedNumberOfRequests - integer, required
* timeInterval - enum(minute, hour, day), required

accountId, clientIp and requestType are fields which help to categorize requests into buckets.
accountId, clientIp can present but be empty in rate limit policy. This mean that such rate limit policy
should be applied independently for each accountId, clientIp. Consider following example:

```yaml
- account_id: very_important_customer
  allowed_number_of_requests: 100000
  timeInterval: MINUTE
- accountId:
  allowedNumberOfRequests: 100
  timeInterval: MINUTE
```
The first Rate Limit Rule in the example above should be applied only to requests from account with id "veryImportantCustomer", second should
be applied independently for each account, if the account id does not equal to "veryImportantCustomer".
This mean that requests from customers with accountId = 10 and accountId = 20 should be counted independently.


Client Request can be mapped to more than one rate limit descriptor. Request should be blocked (Rate Limit Server returned 429)
if violates at least one rate limit rule.

Let's consider another example.
Following rate limit rules are configured:
```yaml
- clientIp:
  allowedNumberOfRequests: 100
  timeInterval: MINUTE

- clientIp:
  allowedNumberOfRequests: 3
  requestType: login
  timeInterval: MINUTE
```
To apply both rules simultaneously to some request, Business Service should send following Rate Limit Request:
```json
[
    {
        "client_ip": "127.0.0.1",
    },
    {
        "client_ip": "127.0.0.1",
        "request_type": "login"
    }
]
```
First Rate Limit Descriptor matches first Rate Limit Rule (allow 100 or less requests per minute from single ip address),
second Rate Limit Descriptor matches second Rate Limit Rule (allow only 3 login requests per minute from single ip address).

### Data Model

![img.png](./statics/Redis-Lab-Data-Model.drawio.png)

**Client Request to Request Descriptor relation**

Multiple requests can be mapped to the same set of the Rate Limit Descriptors.
Consider rate limit policy which allows only 10 requests per minute from specific client ip, without specifying request type.

**Request Descriptor to Rate Limit Rule relation**

Rate limit rule is applied to request if one of the request descriptors match this rule. Rate limit descriptor can't match
more than 1 Rate Limit Rule.

Also, it's possible, that for some reasons Business Service forms descriptor for which rate limit rule is not configured.
This mean that no rule can be applied for given rate limit descriptor, so the relation can have 0-order on the left side.

**Request Descriptor to Redis Key relation**

Each distinct request descriptor is mapped to single Redis key. Value type is not important here and can vary depend
on the required rate limit approach - fixed window, sliding window, token bucket etc.

**Rate Limit Rule to Redis Key relation**

As we support generic rate limit rules, for example separate requests counters for each unique accountId, single
rate limit rule can update and validate multiple counter. In our example we have single rate limit rule which limits
requests from specific customer, the limit is same for all customer - 100 requests per minute, but requests for each customer
are counted independently.

### Fixed window rate limit
Fixed window rate limit assumes that requests count is bind to wall clock minutes, hours, days.
The diagram below illustrates fixed window rate limit with limit of 2 requests per minute.
![img.png](./statics/Redis-Lab-Fixed-Window-Ratelimit.drawio.png)
## Lab tasks

### Mandatory lab task
**You should the project located inside `Code Examples` folder as starting point for lab implementation.**
Thi project contains all required environment setup, all supporting classes (Web API Controller, Model POJO definitions, spring context configuration etc.) and test suite to check lab result.
Please refer to project Readme for environment setup and lab check instructions.


As mandatory assignement, you should provide your own implementation of `fixed window ratelimit`.
You should insert your implementation inside `JedisRateLimitService` class to override default one.
The task is done, when all tests from `FixedWindowRateLimitControllerTest` are passed.

**You gain 100 points, when you successfully finish mandatory task**.


### Optional Tasks:
* Usually, Rate Limit Server should be high-throughput low-latency application. Implement the same Rate Limit Server
  using Spring WebFlux and Lettuce Redis driver library.
* Sliding window rate limit is more precise, add support for sliding window rate limit.
* Lettuce driver support for pipelining, try to use pipelining, where applicable.
* Usually, distributed systems have observability requirement. Try to implement counter of number of total and blocked
  requests for each Rate Limit Rule. You should gather stats for each day with one-hour resolution. You may find Redis
  [Bitfield](https://redis.io/docs/data-types/bitfields/) datatype useful for such counter implementation.
  Redis [Set](https://redis.io/docs/data-types/sets/) structure can help you to store Rate Limit Rules available for each day.

