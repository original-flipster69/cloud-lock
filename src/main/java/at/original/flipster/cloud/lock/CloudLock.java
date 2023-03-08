package at.original.flipster.cloud.lock;

import at.original.flipster.cloud.lock.storage.StorageFactory;
import at.original.flipster.cloud.lock.storage.Vendor;

import java.util.Objects;

public final class CloudLock {

    private final Vendor vendor;
    private final Config config;

    public CloudLock(final Vendor vendor, final Config config) {
        this.vendor = Objects.requireNonNull(vendor);
        this.config = Objects.requireNonNull(config);
    }

    public void doIfLeader(final Runnable action) {
        StorageLock storageLock = new StorageLock(new StorageFactory(config).storage(vendor));
        boolean haveLock = storageLock.acquireLock();
        if (!haveLock) {
            return;
        }
        action.run();
        storageLock.releaseLock();
    }
}
