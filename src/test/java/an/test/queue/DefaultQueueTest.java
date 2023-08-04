package an.test.queue;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultQueueTest {
    private final Random random = new Random();
    private int allCount;
    private int successCount;

    @Test
    public void testOverflow() throws InterruptedException {
        reset();
        Queue<String> queue = new DefaultQueue<>(10);
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            strings.add(String.valueOf(random.nextInt()));
        }
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            executorService.invokeAll(strings.stream().map(s -> (Callable<Void>) () -> {
                allCount++;
                try {
                    queue.put(s);
                    System.out.println("Putting: " + s);
                    successCount++;
                } catch (QueueOverflowException ignore) {
                    System.out.println("Missing: " + s);
                }
                return null;
            }).toList());
        }
        Assert.assertEquals(allCount, 20);
        Assert.assertEquals(successCount, 10);
    }

    private void reset() {
        allCount = 0;
        successCount = 0;
    }
}