package an.test.queue;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DefaultQueue<T> implements Queue<T> {
    private final BlockingQueue<T> internalQueue;

    public DefaultQueue() {
        internalQueue = new LinkedBlockingQueue<>();
    }

    public DefaultQueue(int limit) {
        internalQueue = new LinkedBlockingQueue<>(limit);
    }

    @Override
    public void put(T message) throws QueueOverflowException {
        if (!internalQueue.offer(message)) {
            throw new QueueOverflowException();
        }
    }

    @Override
    public Optional<T> pull(long timeoutMillis) {
        try {
            return Optional.ofNullable(internalQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }
}
