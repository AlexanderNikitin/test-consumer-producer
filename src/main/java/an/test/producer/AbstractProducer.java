package an.test.producer;

import an.test.queue.Queue;
import an.test.queue.QueueOverflowException;
import an.test.ratelimiter.RateLimiter;

public abstract class AbstractProducer<T> implements Producer<T> {
    private RateLimiter rateLimiter;
    private Queue<T> queue;

    @Override
    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void setQueue(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void setProduceRateLimitMinute(int maxCount) {
        if (rateLimiter == null) {
            return;
        }
        rateLimiter.setProduceRateLimitMinute(maxCount);
    }

    @Override
    public void produceSingleMessage() throws RateLimitExceeded {
        if (!rateLimiter.shouldHandle()) {
            throw new RateLimitExceeded();
        }

        try {
            queue.put(createMessage());
        } catch (QueueOverflowException e) {
            throw new RateLimitExceeded(e);
        }
    }

    protected abstract T createMessage();
}
