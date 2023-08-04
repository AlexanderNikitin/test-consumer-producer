package an.test.consumer;

import an.test.util.CommonUtils;

public class FakeConsumer extends AbstractConsumer<String> {
    private static final int HANDLING_STEPS_COUNT = 10;
    private long millisToHandle;

    @Override
    protected void handleMessage(String message) {
        System.out.println("                 " + message);
        long millisDelta = millisToHandle / HANDLING_STEPS_COUNT;
        long now = System.currentTimeMillis();
        for (long time = now, i = 1; time < now + millisToHandle; time += millisDelta, i++) {
            CommonUtils.sleep(millisDelta);
            String progress = ".".repeat((int) i);
            System.out.println("                 " + progress);
        }
        CommonUtils.sleep(millisToHandle);
        System.out.println("                 Completed: " + message);
    }

    public void setMillisToHandle(long millisToHandle) {
        this.millisToHandle = millisToHandle;
    }
}
