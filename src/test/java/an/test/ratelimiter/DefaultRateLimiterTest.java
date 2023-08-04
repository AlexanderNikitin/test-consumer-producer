package an.test.ratelimiter;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static an.test.util.CommonUtils.sleepOneMinute;

public class DefaultRateLimiterTest {

    @Test
    public void testOverflowLimit() {
        RateLimiter rateLimiter = new DefaultRateLimiter();
        RateLimiterStatistic rateLimiterStatistic = new RateLimiterStatistic(rateLimiter);
        try (ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()) {
            executorService.scheduleAtFixedRate(rateLimiterStatistic, 0, 1, TimeUnit.SECONDS);
            sleepOneMinute();
            executorService.shutdown();
        }
        Assert.assertTrue(rateLimiterStatistic.successCount <= 10);
        Assert.assertTrue(rateLimiterStatistic.successCount >= 9);
        Assert.assertTrue(rateLimiterStatistic.allCount >= 58);
    }

    @Test
    public void testNormalLimit() {
        RateLimiter rateLimiter = new DefaultRateLimiter();
        RateLimiterStatistic rateLimiterStatistic = new RateLimiterStatistic(rateLimiter);
        try (ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()) {
            executorService.scheduleAtFixedRate(rateLimiterStatistic, 0, 7, TimeUnit.SECONDS);
            sleepOneMinute();
            executorService.shutdown();
        }
        Assert.assertTrue(rateLimiterStatistic.successCount <= 10);
        Assert.assertTrue(rateLimiterStatistic.successCount >= 9);
        Assert.assertTrue(rateLimiterStatistic.allCount <= 10);
        Assert.assertTrue(rateLimiterStatistic.allCount >= 9);
    }

    private static class RateLimiterStatistic implements Runnable {
        private final RateLimiter rateLimiter;
        public int allCount = 0;
        public int successCount = 0;

        private RateLimiterStatistic(RateLimiter rateLimiter) {
            this.rateLimiter = rateLimiter;
        }

        @Override
        public void run() {
            boolean b = rateLimiter.shouldHandle();
            System.out.println(b);
            allCount++;
            if (b) {
                successCount++;
            }
        }
    }
}