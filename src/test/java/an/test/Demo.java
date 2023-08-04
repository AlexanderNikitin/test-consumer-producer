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
    private static final long USER_MESSAGE_PERIOD_SECONDS = 3;
    private static final int RATE_LIMIT_PER_MINUTE = 10;
    private static final long MILLIS_TO_HANDLE_MESSAGE = 10000; //10seconds

    @Test(timeOut = 1000 * 60 * 2) //2 min
    public void demo() {
        FakeConsumer consumer = new FakeConsumer();
        consumer.setMillisToHandle(MILLIS_TO_HANDLE_MESSAGE);
        FakeProducer producer = new FakeProducer();
        RateLimiter rateLimiter = new DefaultRateLimiter();
        producer.setRateLimiter(rateLimiter);
        //Именно у продюсера ставим rate limit, так как он делегирует его текущему лимиттеру
        producer.setProduceRateLimitMinute(RATE_LIMIT_PER_MINUTE);
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
            }, 0, USER_MESSAGE_PERIOD_SECONDS, TimeUnit.SECONDS);
            consumerService.submit(consumer::consume);
        }
    }
}
