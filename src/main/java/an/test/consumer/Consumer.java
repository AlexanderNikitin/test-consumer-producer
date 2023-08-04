package an.test.consumer;

import an.test.queue.Queue;

public interface Consumer<T> {
    void setQueue(Queue<T> queue);

    void consume();
}
