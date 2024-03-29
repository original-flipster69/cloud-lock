package com.originalflipster.cloud.lock;

import com.originalflipster.cloud.lock.storage.CloudStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

final class StorageLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageLock.class);

    private static final long LIFETIME_MINUTES = 1L;
    private static final long HEARTBEAT_SECONDS = 5L;

    private final CloudStorage providerLock;

    StorageLock(final CloudStorage cloudLock) {
        this.providerLock = Objects.requireNonNull(cloudLock);
    }

    boolean acquireLock() {
        if (!providerLock.lockFileExists()) {
            var gotLock = providerLock.lock();
            if (!gotLock) {
                LOGGER.info("failed acquiring lock");
                return false;
            }
            LOGGER.info("successfully acquired lock");
            return true;
        }

        LocalDateTime lockTime = LocalDateTime.parse(providerLock.getLockContent());
        var minutesBetween = MINUTES.between(lockTime, LocalDateTime.now());

        if (minutesBetween > LIFETIME_MINUTES) {
            //FIXME check necessity
            providerLock.deleteLock();
            boolean gotLock = providerLock.lock();
            if (!gotLock) {
                LOGGER.info("failed acquiring lock");
                return false;
            }
            LOGGER.info("successfully acquired lock");
            return true;
        }
        LOGGER.info("failed acquiring lock");
        return false;
    }

    void releaseLock() {
        if (!providerLock.hasLock()) {
            //FIXME oder fehler?
            return;
        }
        LocalDateTime lockTime = LocalDateTime.parse(providerLock.getLockContent());
        if (!LocalDateTime.now().isAfter(lockTime.plus(HEARTBEAT_SECONDS, SECONDS))) {
            LOGGER.info("waiting minimum lock hold duration until releasing lock again...");
            try {
                //FIXME schedule the release instead?
                Thread.sleep(HEARTBEAT_SECONDS * 1000);
            } catch (Throwable t) {
                //
            }
        }
        providerLock.deleteLock();
        LOGGER.info("lock released");
        providerLock.unlock();
    }
}
