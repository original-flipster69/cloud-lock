package com.originalflipster.cloud.lock;

import com.originalflipster.cloud.lock.storage.CloudStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

final class StorageLock {

    private static final Logger LOG = LoggerFactory.getLogger(StorageLock.class);

    private static final long LIFETIME_MINUTES = 1L;
    private static final long HEARTBEAT_SECONDS = 5L;

    private final CloudStorage storage;

    StorageLock(final CloudStorage storage) {
        this.storage = Objects.requireNonNull(storage);
    }

    boolean acquireLock() {
        if (!storage.lockFileExists()) {
            var gotLock = storage.lock();
            if (!gotLock) {
                LOG.info("failed acquiring lock");
                return false;
            }
            LOG.info("successfully acquired lock");
            return true;
        }

        LocalDateTime lockTime = LocalDateTime.parse(storage.getLockContent());
        var minutesBetween = MINUTES.between(lockTime, LocalDateTime.now());

        if (minutesBetween > LIFETIME_MINUTES) {
            //FIXME check necessity
            storage.deleteLock();
            boolean gotLock = storage.lock();
            if (!gotLock) {
                LOG.info("failed acquiring lock");
                return false;
            }
            LOG.info("successfully acquired lock");
            return true;
        }
        LOG.info("failed acquiring lock");
        return false;
    }

    void releaseLock() {
        if (!storage.hasLock()) {
            LOG.warn("trying to release lock, but no lock present");
            return;
        }
        LocalDateTime lockTime = LocalDateTime.parse(storage.getLockContent());
        if (!LocalDateTime.now().isAfter(lockTime.plus(HEARTBEAT_SECONDS, SECONDS))) {
            LOG.info("waiting minimum lock hold duration until releasing lock again...");
            try {
                //FIXME schedule the release instead?
                Thread.sleep(HEARTBEAT_SECONDS * 1000);
            } catch (Throwable t) {
                //
            }
        }
        storage.deleteLock();
        LOG.info("lock released");
        storage.unlock();
    }
}
