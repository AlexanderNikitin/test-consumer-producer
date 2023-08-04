package an.test.consumer;

import an.test.queue.Queue;

import java.util.Optional;

public abstract class AbstractConsumer<T> implements Consumer<T> {
    private static final int DEFAULT_PULL_TIMEOUT_MILLIS = 1000;
    private Queue<T> queue;

    @Override
    public void setQueue(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void consume() {
        while (true) {
            Optional<T> pulled = queue.pull(DEFAULT_PULL_TIMEOUT_MILLIS);
            pulled.ifPresent(this::handleMessage);
        }
    }

    protected abstract void handleMessage(T message);
}
