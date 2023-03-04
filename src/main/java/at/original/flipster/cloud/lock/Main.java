package at.original.flipster.cloud.lock;

public class Main {

    public static void main(String[] args) {

//        Runnable r =
//                () -> {
//                    var lock = new GcpLock();
//                    try {
//                        Thread.sleep((long) (Math.random() * 3000));
//                    } catch(Throwable t) {
//                        // ignored
//                    }
//                    lock.acquireLock();
//                    try {
//                        Thread.sleep((long) (Math.random() * 3000));
//                    } catch(Throwable t) {
//                        // ignored
//                    }
//                    lock.releaseLock();
//                };
//
//        for (int i = 0; i < 10; i++) {
//            new Thread(r).start();
//        }

                Runnable r =
                () -> {
                    var lock = new AzureBlobStorage();
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
