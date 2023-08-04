package an.test.util;

import java.util.concurrent.TimeUnit;

public class CommonUtils {
    public static void sleepOneMinute() {
        sleep(TimeUnit.MINUTES.toMillis(1));
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }
}
