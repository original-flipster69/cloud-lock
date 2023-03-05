package at.original.flipster.cloud.lock;

import at.original.flipster.cloud.lock.storage.StorageFactory;
import at.original.flipster.cloud.lock.storage.Vendor;

import java.util.Objects;

public final class CloudLock {

    private final Vendor vendor;
    private final String bucketName;
    private final String lockFile;

    public CloudLock(final Vendor vendor, final String bucketName, final String lockFile) {
        this.vendor = Objects.requireNonNull(vendor);
        this.bucketName = Objects.requireNonNull(bucketName);
        this.lockFile = Objects.requireNonNull(lockFile);
    }

    public void doOnlyAsLeader(final Runnable action) {
        StorageLock storageLock = new StorageLock(new StorageFactory(bucketName, lockFile).storage(vendor));
        boolean haveLock = storageLock.acquireLock();
        if (!haveLock) {
            return;
        }
        action.run();
        storageLock.releaseLock();
    }
}
