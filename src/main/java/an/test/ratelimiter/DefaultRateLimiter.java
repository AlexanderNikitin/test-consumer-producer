package an.test.ratelimiter;

import java.util.concurrent.TimeUnit;

public class DefaultRateLimiter implements RateLimiter {
    private int minuteMaxCount = DEFAULT_MINUTE_MAX_COUNT;
    private long calculatedDelay = TimeUnit.MINUTES.toMillis(1) / minuteMaxCount;
    private long lastSuccessHandle;

    public DefaultRateLimiter() {
        initDelay();
    }

    @Override
    public void setRateLimitMinute(int maxCount) {
        minuteMaxCount = maxCount;
        initDelay();
    }

    @Override
    public boolean shouldHandle() {
        long now = System.currentTimeMillis();
        if (now - lastSuccessHandle > calculatedDelay) {
            lastSuccessHandle = now;
            return true;
        }
        return false;
    }

    private void initDelay() {
        if (minuteMaxCount > 0) {
            calculatedDelay = TimeUnit.MINUTES.toMillis(1) / minuteMaxCount;
        } else {
            calculatedDelay = 0;
        }
    }
}
