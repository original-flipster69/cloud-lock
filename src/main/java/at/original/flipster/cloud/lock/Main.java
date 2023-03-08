package at.original.flipster.cloud.lock;

import at.original.flipster.cloud.lock.storage.Vendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String BUCKET = "tyler-lockett";
    private static final String FILE = "leader.txt";

    public static void main(String[] args) {

        final Vendor vendor = Vendor.AZURE;
        LOGGER.info("logging with {}", vendor);
        CloudLock cloudlock = new CloudLock(vendor, BUCKET, FILE);

        Runnable r =
                () -> {
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (Throwable t) {
                        // ignored
                    }
                    cloudlock.doIfLeader(() -> {
                        try {
                            Thread.sleep((long) (Math.random() * 1000));
                        } catch (Throwable t) {
                            // ignored
                        }
                    });
                };

        for (int i = 0; i < 10; i++) {
            new Thread(r).start();
        }
    }
}
