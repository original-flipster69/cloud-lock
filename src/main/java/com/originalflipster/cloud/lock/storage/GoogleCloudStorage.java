package com.originalflipster.cloud.lock.storage;


import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

final class GoogleCloudStorage implements CloudStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCloudStorage.class);

    private final String bucketName;
    private final String lockFile;

    private boolean lock = false;

    private Storage storage;
    private Blob blob;

    GoogleCloudStorage(final String bucketName, final String lockFile) {
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
            getStorage().create(BlobInfo.newBuilder(bucketName, lockFile).build(), LocalDateTime.now().toString().getBytes(StandardCharsets.UTF_8), Storage.BlobTargetOption.doesNotExist());
            lock = true;
        } catch (StorageException se) {
            LOGGER.debug("failed to acquire lock - lock file already exists");
            return false;
        }
        return true;
    }

    private Blob getBlob() {
        if (blob == null) {
            blob = getStorage().get(bucketName, lockFile);
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
        return lock;
    }

    @Override
    public void deleteLock() {
        if (hasLock()) {
            getBlob().delete();
            return;
        }
        getBlob().delete();
    }

    @Override
    public void unlock() {
        lock = false;
    }
}
