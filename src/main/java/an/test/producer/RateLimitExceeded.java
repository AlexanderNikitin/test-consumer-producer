package an.test.producer;

public class RateLimitExceeded extends Exception {
    public RateLimitExceeded() {
    }

    public RateLimitExceeded(Throwable cause) {
        super(cause);
    }
}
