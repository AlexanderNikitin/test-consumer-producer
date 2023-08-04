package an.test.producer;

import an.test.queue.Queue;
import an.test.queue.QueueOverflowException;
import an.test.ratelimiter.RateLimiter;

public abstract class AbstractProducer<T> implements Producer<T> {
    private RateLimiter rateLimiter;
    private Queue<T> queue;
    private int rateLimitMinute;

    @Override
    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
        rateLimiter.setRateLimitMinute(rateLimitMinute);
    }

    @Override
    public void setQueue(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void setProduceRateLimitMinute(int maxCount) {
        rateLimitMinute = maxCount;
        if (rateLimiter == null) {
            return;
        }
        rateLimiter.setRateLimitMinute(rateLimitMinute);
    }

    @Override
    public void produceSingleMessage() throws RateLimitExceeded {
        if (rateLimiter == null || rateLimiter.shouldHandle()) {
            try {
                queue.put(createMessage());
            } catch (QueueOverflowException e) {
                throw new RateLimitExceeded(e);
            }
        } else {
            throw new RateLimitExceeded();
        }
    }

    protected abstract T createMessage();
}
