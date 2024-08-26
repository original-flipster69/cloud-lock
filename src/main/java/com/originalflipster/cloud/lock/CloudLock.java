package com.originalflipster.cloud.lock;

import com.originalflipster.cloud.lock.storage.CloudStorage;
import com.originalflipster.cloud.lock.storage.StorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * entry point for distributed locking via cloud storage
 */
public final class CloudLock {

    private static final Logger LOG = LoggerFactory.getLogger(CloudLock.class);

    private static final long LIFETIME_MINUTES = 1L;
    private static final long HEARTBEAT_SECONDS = 5L;
    private static final String PROPS_FILE = "cloud-lock.properties";

    private final CloudStorage storage;

    /**
     * constructor for passing vendor (= cloud provider) and config manually upon instantiation
     */
    public CloudLock(final Vendor vendor, final Config config) {
        this.storage = new StorageFactory(Objects.requireNonNull(vendor), Objects.requireNonNull(config)).storage();
    }

    /**
     * no-args constructor for instantiation, vendor and configuration have to be provided via a <code>config-lock.properties</code> file
     */
    public CloudLock() {
        Properties props = new Properties();
        URL propsFile = CloudLock.class.getClassLoader().getResource(PROPS_FILE);
        if(propsFile == null) {
            LOG.error("abort mission, missing config file '{}'", PROPS_FILE);
            throw new IllegalStateException("abort mission, missing config file");
        }
        try {
            props.load(new FileInputStream(propsFile.getPath()));
        } catch (IOException ioe) {
            LOG.error("abort mission, cannot read config file '{}'", PROPS_FILE);
            throw new IllegalStateException("abort mission, cannot read config file", ioe);
        }
        this.storage = new StorageFactory(Objects.requireNonNull(Vendor.valueOf(props.getProperty("vendor"))), new Config(props.getProperty("bucketName"), props.getProperty("lockFile"), props.getProperty("endpoint"), props.getProperty("accessId"), props.getProperty("accessKey"))).storage();
    }

    /**
     * this is where the magic happens for you
     * use this method with your configured instance of <code>CloudLock</code> and pass the workload you only want your current leader instance to execute as a {@link Runnable}
     */
    public void doLocked(final Runnable action) {
        boolean haveLock = acquireLock();
        if (!haveLock) {
            return;
        }
        action.run();
        releaseLock();
    }

    private boolean acquireLock() {
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

    private void releaseLock() {
        if (!storage.hasLock()) {
            LOG.warn("trying to release lock, but no lock present");
            return;
        }
        LocalDateTime lockTime = LocalDateTime.parse(storage.getLockContent());
        if (!LocalDateTime.now().isAfter(lockTime.plus(HEARTBEAT_SECONDS, SECONDS))) {
            LOG.info("waiting minimum lock hold duration until releasing lock again...");
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                storage.deleteLock();
                LOG.info("lock released");
                storage.unlock();
            }, HEARTBEAT_SECONDS, TimeUnit.SECONDS);
        }
    }
}
