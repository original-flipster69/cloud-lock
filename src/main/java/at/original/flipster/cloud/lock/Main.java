package at.original.flipster.cloud.lock;

import at.original.flipster.cloud.lock.storage.Vendor;

public class Main {

    private static final String BUCKET = "tyler-lockett";
    private static final String FILE = "leader.txt";

    public static void main(String[] args) {

        final Vendor vendor = Vendor.GCP;
        System.out.println("locking with: " + vendor);
        CloudLock cloudlock = new CloudLock(vendor, BUCKET, FILE);

        Runnable r =
                () -> {
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (Throwable t) {
                        // ignored
                    }
                    cloudlock.doOnlyAsLeader(() -> {
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
