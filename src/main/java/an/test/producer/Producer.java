package an.test.producer;

import an.test.queue.Queue;
import an.test.ratelimiter.RateLimiter;

public interface Producer<T> {
    void setRateLimiter(RateLimiter rateLimiter);

    void setQueue(Queue<T> queue);

    void setProduceRateLimitMinute(int maxCount);

    void produceSingleMessage() throws RateLimitExceeded;
}
