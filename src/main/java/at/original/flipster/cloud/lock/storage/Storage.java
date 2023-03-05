package at.original.flipster.cloud.lock.storage;

public interface Storage {
    boolean lockFileExists();

    boolean lock();

    String getLockContent();

    boolean hasLock();

    void deleteLock();

    void unlock();
}
