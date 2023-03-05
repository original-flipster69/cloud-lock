package at.original.flipster.cloud.lock;

public interface Storage {
    boolean lockFileExists();

    boolean lock();

    String getLockContent();

    boolean hasLock();

    void deleteLock();

    void unlock();
}
