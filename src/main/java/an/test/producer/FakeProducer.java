package an.test.producer;

import java.util.Random;

public class FakeProducer extends AbstractProducer<String> {
    private final Random random = new Random();

    @Override
    protected String createMessage() {
        String message = String.valueOf(random.nextInt());
        System.out.println("Sent " + message);
        return message;
    }
}
