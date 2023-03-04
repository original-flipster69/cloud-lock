package at.original.flipster.cloud.lock;


import com.google.cloud.storage.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

public final class GcpLock implements CloudLock {

    private static final String BUCKET_NAME = "tyler-lockett";
    private static final String FILE = "leader.txt";

    private static final long LIFETIME_MINUTES = 1L;
    private static final long HEARTBEAT_SECONDS = 5L;

    private Lock lock = null;

    public void acquireLock() {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get(BUCKET_NAME);

        Blob blob = bucket.get(FILE);

        if (blob == null) {
            try {
                blob = bucket.create(FILE, LocalDateTime.now().toString().getBytes(StandardCharsets.UTF_8), Bucket.BlobTargetOption.doesNotExist());
            } catch (StorageException se) {
                //System.out.println("failed getting lock");
                return;
            }
            lock = new Lock(blob.getBlobId(), LocalDateTime.now());
            System.out.println("acquired lock!!!");
            return;
        }

        String content = null;
        try {
            content = new String(blob.getContent());
        } catch (StorageException se) {
            //System.out.println("failed reading blob");
            return;
        }
        var lockTime = LocalDateTime.parse(content);

        var minutesBetween = MINUTES.between(lockTime, LocalDateTime.now());

        if (minutesBetween > LIFETIME_MINUTES) {
            blob.delete();
            try {
                bucket.create(FILE, LocalDateTime.now().toString().getBytes(StandardCharsets.UTF_8), Bucket.BlobTargetOption.doesNotExist());
            } catch (StorageException se) {
                //System.out.println("failed getting lock");
                return;
            }
            lock = new Lock(blob.getBlobId(), LocalDateTime.now());
            System.out.println("acquired lock via timeout!");
            return;
        }
        //System.out.println("no lock for now...");
    }

    public void releaseLock() {
        if (lock == null) {
            return;
        }
        Storage storage = StorageOptions.getDefaultInstance().getService();

        Blob lockBlob = storage.get(lock.blob());
        String content = null;
        content = new String(lockBlob.getContent());
        var lockTime = LocalDateTime.parse(content);
        if(!LocalDateTime.now().isAfter(lockTime.plus(HEARTBEAT_SECONDS, SECONDS))) {
            System.out.println("waiting...");
            try {
                //FIXME schedule the release instead?
                Thread.sleep(HEARTBEAT_SECONDS * 1000);
            } catch(Throwable t) {
                //
            }
        }
        storage.delete(lock.blob());
        System.out.println("released lock");
        lock = null;
    }
}
