package at.original.flipster.cloud.lock;

public class Main {

    public static void main(String[] args) {

        final Vendor vendor = Vendor.GCP;
        System.out.println("locking with: " + vendor);

                Runnable r =
                () -> {
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch(Throwable t) {
                        // ignored
                    }
                    new LeaderOnlyExecution().onlyExecuteAsLeader(() -> {try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch(Throwable t) {
                        // ignored
                    }}, vendor);
                };

        for (int i = 0; i < 10; i++) {
            new Thread(r).start();
        }
    }
}
