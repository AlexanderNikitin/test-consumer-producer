package an.test.queue;

import java.util.Optional;

public interface Queue<T> {
    void put(T message) throws QueueOverflowException;

    Optional<T> pull(long timeoutMillis);
}
