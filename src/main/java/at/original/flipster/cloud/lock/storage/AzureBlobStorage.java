package at.original.flipster.cloud.lock.storage;

import com.azure.core.util.BinaryData;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;

import java.time.LocalDateTime;
import java.util.Objects;

class AzureBlobStorage implements Storage {

    private final String containerName;
    private final String lockFile;
    private final DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();

    private Lock<Void> lock = null;

    private BlobServiceClient serviceClient;
    private BlobContainerClient containerClient;
    private BlobClient blobClient;

    AzureBlobStorage(final String containerName, final String lockFile){
        this.containerName = Objects.requireNonNull(containerName);
        this.lockFile = Objects.requireNonNull(lockFile);
    }

    private BlobServiceClient getServiceClient() {
        if (serviceClient == null) {
            serviceClient = new BlobServiceClientBuilder()
                    .endpoint("https://tylerlockett.blob.core.windows.net/")
                    .credential(defaultCredential)
                    .buildClient();
        }
        return serviceClient;
    }

    private BlobContainerClient getContainerClient() {
        if (containerClient == null) {
            containerClient = getServiceClient().getBlobContainerClient(containerName);
        }
        return containerClient;
    }

    private BlobClient getBlobClient() {
        if (blobClient == null) {
            blobClient = getContainerClient().getBlobClient(lockFile);
        }
        return blobClient;
    }

    @Override
    public boolean lockFileExists() {
        return getBlobClient().exists();
    }

    @Override
    public boolean lock() {
        try {
            getBlobClient().upload(BinaryData.fromString(LocalDateTime.now().toString()), false);
            lock = new Lock<>(null, LocalDateTime.now());
        } catch (BlobStorageException bse) {
            return false;
        }
        return true;
    }

    @Override
    public String getLockContent() {
        return new String(getBlobClient().downloadContent().toBytes());
    }

    @Override
    public boolean hasLock() {
        return lock != null;
    }

    @Override
    public void deleteLock() {
        getBlobClient().delete();
    }

    @Override
    public void unlock() {
        lock = null;
    }
}
