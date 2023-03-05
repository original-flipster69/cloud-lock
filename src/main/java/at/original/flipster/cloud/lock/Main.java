package at.original.flipster.cloud.lock;

public class Main {

    public static void main(String[] args) {

        final String mode = "gcp";
        System.out.println("locking with: " + mode);

                Runnable r =
                () -> {
                    var provider = "gcp".equals(mode) ? new GoogleCloudStorage("tyler-lockett", "leader.txt") : new AzureBlobStorage("tylerlockett", "leader.txt");
                    var lock = new CloudLock(provider);
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch(Throwable t) {
                        // ignored
                    }
                    lock.acquireLock();
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch(Throwable t) {
                        // ignored
                    }
                    lock.releaseLock();
                };

        for (int i = 0; i < 10; i++) {
            new Thread(r).start();
        }
    }
}
