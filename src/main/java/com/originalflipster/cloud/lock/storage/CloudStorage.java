package com.originalflipster.cloud.lock.storage;

public interface CloudStorage {
    boolean lockFileExists();

    boolean lock();

    String getLockContent();

    boolean hasLock();

    void deleteLock();

    void unlock();
}
