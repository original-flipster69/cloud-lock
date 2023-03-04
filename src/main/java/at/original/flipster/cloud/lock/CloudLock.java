package at.original.flipster.cloud.lock;

public interface CloudLock {

    void acquireLock();

    void releaseLock();
}
