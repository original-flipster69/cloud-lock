package com.originalflipster.cloud.lock;

import com.originalflipster.cloud.lock.storage.Vendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    //private static final String ENDPOINT = "https://tylerlockett.blob.core.windows.net/";
    private static final String ENDPOINT = "https://oss-eu-central-1.aliyuncs.com";
    private static final String BUCKET = "tyler-lockett";
    private static final String FILE = "leader.txt";

    public static void main(String[] args) {

        final String accessId = System.getenv("ACCESS_ID");
        final String accessKey = System.getenv("ACCESS_KEY");

        final Vendor vendor = Vendor.ALIBABA;
        LOGGER.info("logging with {}", vendor);
        CloudLock cloudlock = new CloudLock(vendor, new Config(BUCKET, FILE, ENDPOINT, accessId, accessKey));

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
                            System.out.println("pipsi pipsi puuuuuuu");
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
