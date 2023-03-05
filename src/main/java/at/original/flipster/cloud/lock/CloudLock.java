package at.original.flipster.cloud.lock;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

public class CloudLock {

    private static final long LIFETIME_MINUTES = 1L;
    private static final long HEARTBEAT_SECONDS = 5L;

    private final ProviderLock providerLock;

    public CloudLock(final ProviderLock cloudLock) {
        this.providerLock = Objects.requireNonNull(cloudLock);
    }

    public void acquireLock() {
        if (!providerLock.lockFileExists()) {
            var gotLock = providerLock.lock();
            if (!gotLock) {
                return;
            }
            System.out.println("acquired lock");
        }

        LocalDateTime lockTime = LocalDateTime.parse(providerLock.getLockContent());
        var minutesBetween = MINUTES.between(lockTime, LocalDateTime.now());

        if (minutesBetween > LIFETIME_MINUTES) {
            //FIXME check necessity
            providerLock.deleteLock();
            boolean gotLock = providerLock.lock();
            if (!gotLock) {
                return;
            }
            System.out.println("acquired lock");
        }
    }

    public void releaseLock() {
        if(!providerLock.hasLock()) {
            //FIXME oder fehler?
            return;
        }
        LocalDateTime lockTime = LocalDateTime.parse(providerLock.getLockContent());
        if(!LocalDateTime.now().isAfter(lockTime.plus(HEARTBEAT_SECONDS, SECONDS))) {
            System.out.println("waiting...");
            try {
                //FIXME schedule the release instead?
                Thread.sleep(HEARTBEAT_SECONDS * 1000);
            } catch(Throwable t) {
                //
            }
        }
        providerLock.deleteLock();
        System.out.println("released lock");
        providerLock.unlock();
    }
}
