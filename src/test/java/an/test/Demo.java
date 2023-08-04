package an.test;

import an.test.consumer.FakeConsumer;
import an.test.producer.FakeProducer;
import an.test.producer.RateLimitExceeded;
import an.test.queue.DefaultQueue;
import an.test.queue.Queue;
import an.test.queue.QueueOverflowException;
import an.test.ratelimiter.DefaultRateLimiter;
import an.test.ratelimiter.RateLimiter;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Demo {
    @Test
    public void demo() {
        FakeConsumer consumer = new FakeConsumer();
        consumer.setMillisToHandle(10000);
        FakeProducer producer = new FakeProducer();
        RateLimiter rateLimiter = new DefaultRateLimiter();
        producer.setRateLimiter(rateLimiter);
        Queue<String> queue = new DefaultQueue<>();
        consumer.setQueue(queue);
        producer.setQueue(queue);
        try (ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
             ExecutorService consumerService = Executors.newSingleThreadScheduledExecutor()) {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                try {
                    producer.produceSingleMessage();
                } catch (RateLimitExceeded e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof QueueOverflowException) {
                        System.out.println("Rejected by queue overflow");
                    } else {
                        System.out.println("Rejected");
                    }
                }
            }, 0, 3, TimeUnit.SECONDS);
            consumerService.submit(consumer::consume);
        }
    }
}
