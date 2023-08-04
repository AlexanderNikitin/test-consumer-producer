package an.test.queue;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultQueueTest {
    private final Random random = new Random();
    private final AtomicInteger allCount = new AtomicInteger();
    private final AtomicInteger successCount = new AtomicInteger();
    private Queue<String> queue;

    @Test
    public void testOverflow() {
        reset();
        pushToQueue(20);
        Assert.assertEquals(allCount.get(), 20);
        Assert.assertEquals(successCount.get(), 10);
    }

    @Test
    public void testOverflowWithPullingAsync() throws InterruptedException {
        reset();
        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            executorService.invokeAll(
                    Arrays.asList(
                            () -> {
                                pullFromQueue(10);
                                return null;
                            },
                            () -> {
                                pushToQueue(30);
                                return null;
                            }));
        }
        Assert.assertEquals(allCount.get(), 30);
        Assert.assertEquals(successCount.get(), 20);
    }

    private void pushToQueue(int count) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(count)) {
            List<String> strings = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                strings.add(String.valueOf(random.nextInt()));
            }
            executorService.invokeAll(strings.stream().map(s -> (Callable<Void>) () -> {
                allCount.incrementAndGet();
                try {
                    queue.put(s);
                    System.out.println("Putting: " + s);
                    successCount.incrementAndGet();
                } catch (QueueOverflowException ignore) {
                    System.out.println("Missing: " + s);
                }
                return null;
            }).toList());
        } catch (InterruptedException ignore) {
        }
    }

    private void pullFromQueue(int count) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(count)) {
            for (int i = 0; i < count; i++) {
                executorService.submit(() -> {
                    queue.pull(1000)
                            .ifPresent(s -> System.out.println("      Pulled: " + s));
                    return null;
                });
            }
        }
    }

    private void reset() {
        allCount.set(0);
        successCount.set(0);
        queue = new DefaultQueue<>(10);
    }
}