package an.test.queue;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaQueue<T> implements Queue<T> {
    private static final String TOPIC = "topic1";

    private final KafkaProducer<String, T> producer;
    private final KafkaConsumer<String, T> consumer;
    private final Deque<T> messages = new LinkedList<>();

    public KafkaQueue() {
        producer = getKafkaProducer();
        consumer = getKafkaConsumer();
    }

    @Override
    public void put(T message) throws QueueOverflowException {
        final ProducerRecord<String, T> record = new ProducerRecord<>(TOPIC, "key", message);
        Future<RecordMetadata> future = producer.send(record);
        try {
            future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new QueueOverflowException();
        }
    }

    @Override
    public Optional<T> pull(long timeoutMillis) {
        if (messages.isEmpty()) {
            ConsumerRecords<String, T> poll = consumer.poll(timeoutMillis);
            for (var p : poll) {
                messages.add(p.value());
            }
        }
        return Optional.ofNullable(messages.poll());
    }

    private KafkaProducer<String, T> getKafkaProducer() {
        Properties config = new Properties();
        config.put("client.id", "test-queue");
        config.put("bootstrap.servers", "host1:9092,host2:9092");
        config.put("acks", "all");
        return new KafkaProducer<>(config);
    }

    private KafkaConsumer<String, T> getKafkaConsumer() {
        Properties config = new Properties();
        config.put("client.id", "test-queue");
        config.put("bootstrap.servers", "host1:9092,host2:9092");
        config.put("acks", "all");
        return new KafkaConsumer<>(config);
    }
}
