# HTTP Load Testing Tool

A simple, single-file Spring Boot application for load testing HTTP endpoints with configurable parameters.

## Features
- Concurrent request execution using configurable thread pool
- Configurable request payload size
- Customizable number of total requests
- Detailed performance metrics output
- Simple JSON-based request/response format

## Prerequisites
- Java 17 or higher
- Maven (for building)

## Building
```bash
mvn clean package
```

## Running
Basic usage:
```bash
java -jar clientrunner-0.0.1-SNAPSHOT.jar
```

With custom configuration:
```bash
java -jar clientrunner-0.0.1-SNAPSHOT.jar \
  --benchmark.target-url=http://localhost:8082/api/files/write \
  --benchmark.num-threads=100 \
  --benchmark.total-requests=1000 \
  --benchmark.content-size-kb=1000
```

## Configuration Parameters

| Parameter | Default Value | Description |
|-----------|---------------|-------------|
| benchmark.target-url | http://localhost:8082/api/files/write | Target endpoint URL |
| benchmark.num-threads | 100 | Number of concurrent threads |
| benchmark.total-requests | 1000 | Total number of requests to send |
| benchmark.content-size-kb | 1000 | Size of request payload in KB |

## Output Example
```
Starting Benchmark with configuration:
Target URL: http://localhost:8082/api/files/write
Number of Threads: 100
Total Requests: 1000
Content Size (KB): 1000

Benchmark Results:
==================
Total Time: 25.43 seconds
Successful Requests: 985
Failed Requests: 15
Requests/Second: 38.73
Throughput: 37.82 MB/s
```

## Expected Response Format
The target endpoint should return a JSON response in the following format:
```json
{
    "success": true
}
```

## Notes
- The tool generates random string content for each request
- Requests timeout after 5 minutes
- Thread pool is automatically cleaned up after completion
- Failed requests are logged to stderr
- The application will exit automatically after completing the benchmark

## Use Cases
- Load testing HTTP endpoints
- Performance testing of file upload endpoints
- Stress testing web services
- Measuring throughput of REST APIs

## Limitations
- Single endpoint testing only
- Fixed request/response format
- No detailed timing metrics per request
- No support for authentication
- No support for custom headers

## Contributing
This is a simple, single-file application designed for quick testing scenarios. Feel free to modify the code to suit your specific needs.

## License
MIT License - feel free to use and modify as needed.

---

Note: This is a development tool and should be used with caution in production environments. Always ensure you have permission to perform load testing on the target system.