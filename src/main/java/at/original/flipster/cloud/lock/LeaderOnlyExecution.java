package at.original.flipster.cloud.lock;

public class LeaderOnlyExecution {

    public void onlyExecuteAsLeader(final Runnable action, final Vendor vendor) {
        CloudLock cloudLock = new CloudLock(getStorage(vendor));
        boolean haveLock = cloudLock.acquireLock();
        if (!haveLock) {
            return;
        }
        action.run();
        cloudLock.releaseLock();
    }

    private Storage getStorage(final Vendor vendor) {
        switch (vendor) {
            case GCP:
                return new GoogleCloudStorage("tyler-lockett", "leader.txt");
            case AZURE:
                return new AzureBlobStorage("tylerlockett", "leader.txt");
        }
        throw new IllegalArgumentException("no storage implementation found for vendor = " + vendor);
    }
}
