package org.example.clientrunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootApplication
public class ClientrunnerApplication implements CommandLineRunner {


    @Value("${benchmark.target-url:http://localhost:8082/api/files/write}")
    private String targetUrl;

    @Value("${benchmark.num-threads:100}")
    private int numThreads;

    @Value("${benchmark.total-requests:1000}")
    private int totalRequests;

    @Value("${benchmark.content-size-kb:1000}")
    private int contentSizeKb;


    private final RestTemplate restTemplate = new RestTemplate();
    private  ExecutorService executorService;

    public static void main(String[] args) {
        SpringApplication.run(ClientrunnerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting Benchmark with configuration:");
        System.out.println("Target URL: " + targetUrl);
        System.out.println("Number of Threads: " + numThreads);
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Content Size (KB): " + contentSizeKb);
        this.executorService = Executors.newFixedThreadPool(numThreads);

        System.out.println("Starting Benchmark...");
        runBenchmark();
        System.exit(0);
    }

    private void runBenchmark() throws InterruptedException {


        CountDownLatch latch = new CountDownLatch(totalRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // Generate sample content
        String content = generateContent(contentSizeKb);
        FileRequest request = new FileRequest(content);

        long startTime = System.currentTimeMillis();

        // Submit requests
        for (int i = 0; i < totalRequests; i++) {
            executorService.submit(() -> {
                try {
                    ResponseEntity<FileResponse> response = restTemplate.postForEntity(
                            targetUrl,
                            request,
                            FileResponse.class
                    );

                    if (response.getStatusCode() == HttpStatus.OK &&
                            response.getBody() != null &&
                            response.getBody().isSuccess()) {
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.err.println("Request failed: " + e.getMessage());
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for completion
        latch.await(5, TimeUnit.MINUTES);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // Calculate results
        long duration = System.currentTimeMillis() - startTime;
        double seconds = duration / 1000.0;
        double requestsPerSecond = successCount.get() / seconds;
        double mbPerSecond = (requestsPerSecond * contentSizeKb) / 1024;

        // Print results
        System.out.println("\nBenchmark Results:");
        System.out.println("==================");
        System.out.println("Total Time: " + String.format("%.2f", seconds) + " seconds");
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + errorCount.get());
        System.out.println("Requests/Second: " + String.format("%.2f", requestsPerSecond));
        System.out.println("Throughput: " + String.format("%.2f", mbPerSecond) + " MB/s");
    }

    private String generateContent(int sizeKB) {
        StringBuilder sb = new StringBuilder(sizeKB * 1024);
        Random random = new Random();
        for (int i = 0; i < sizeKB * 1024; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
        }
        return sb.toString();
    }

}
