package at.original.flipster.cloud.lock;

import com.azure.core.util.BinaryData;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

public class AzureBlobStorage implements CloudLock{

    private static final String CONTAINER_NAME = "tylerlockett";
    private static final String FILE = "leader.txt";

    private static final long LIFETIME_MINUTES = 1L;
    private static final long HEARTBEAT_SECONDS = 5L;

    private final DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();

    private AzureLock lock = null;

    public void acquireLock() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://tylerlockett.blob.core.windows.net/")
                .credential(defaultCredential)
                .buildClient();

                //FIXME create if not exists
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        BlobClient blobClient = blobContainerClient.getBlobClient(FILE);
        if(!blobClient.exists()) {
            try {
                blobClient.upload(BinaryData.fromString(LocalDateTime.now().toString()), false);
            } catch(BlobStorageException bse) {
                return;
            }
            lock = new AzureLock();
            System.out.println("acquired lock!!!");
            return;
        }

        String content = null;
        content = new String(blobClient.downloadContent().toBytes());
        var lockTime = LocalDateTime.parse(content);

        var minutesBetween = MINUTES.between(lockTime, LocalDateTime.now());

        if (minutesBetween > LIFETIME_MINUTES) {
            blobClient.delete();
            try {
                blobClient.upload(BinaryData.fromString(LocalDateTime.now().toString()));
            } catch (BlobStorageException bse) {
                return;
            }
            lock = new AzureLock();
            System.out.println("acquired lock via timeout!");
            return;
        }
        //System.out.println("no lock for now...");
    }

    public void releaseLock() {
        if (lock == null) {
            return;
        }
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://tylerlockett.blob.core.windows.net/")
                .credential(defaultCredential)
                .buildClient();

        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        BlobClient blobClient = blobContainerClient.getBlobClient(FILE);
        String content = new String(blobClient.downloadContent().toBytes());

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
        blobClient.delete();
        System.out.println("released lock");
        lock = null;
    }
}
