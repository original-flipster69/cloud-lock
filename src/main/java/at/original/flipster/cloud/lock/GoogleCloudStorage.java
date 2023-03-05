package at.original.flipster.cloud.lock;


import com.google.cloud.storage.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

public final class GoogleCloudStorage implements Storage {

    private final String bucketName;
    private final String lockFile;

    private Lock<BlobId> lock = null;

    private com.google.cloud.storage.Storage storage;
    private Bucket bucket;
    private Blob blob;

    public GoogleCloudStorage(final String bucketName, final String lockFile) {
        this.bucketName = Objects.requireNonNull(bucketName);
        this.lockFile = Objects.requireNonNull(lockFile);
    }

    @Override
    public boolean lockFileExists() {
        return getBlob() != null;
    }

    @Override
    public boolean lock() {
        try {
            Blob blob = getBucket().create(lockFile, LocalDateTime.now().toString().getBytes(StandardCharsets.UTF_8), Bucket.BlobTargetOption.doesNotExist());
            lock = new Lock<>(blob.getBlobId(), LocalDateTime.now());
        } catch (StorageException se) {
            return false;
        }
        return true;
    }

    private Bucket getBucket() {
        if (bucket == null) {
            bucket = getStorage().get(bucketName);
        }
        return bucket;
    }

    private Blob getBlob() {
        if (blob == null) {
            blob = getBucket().get(lockFile);
        }
        return blob;
    }

    private com.google.cloud.storage.Storage getStorage() {
        if (storage == null) {
            storage = StorageOptions.getDefaultInstance().getService();
        }
        return storage;
    }

    @Override
    public String getLockContent() {
        return new String(getBlob().getContent());
    }

    @Override
    public boolean hasLock() {
        return lock != null;
    }

    @Override
    public void deleteLock() {
        if (hasLock()) {
            getStorage().delete(lock.blob());
            return;
        }
        getBlob().delete();
    }

    @Override
    public void unlock() {
        lock = null;
    }
}
