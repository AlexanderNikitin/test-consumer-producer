package an.test.ratelimiter;

public interface RateLimiter {
    int DEFAULT_MINUTE_MAX_COUNT = 10;

    void setProduceRateLimitMinute(int maxCount);

    boolean shouldHandle();
}
