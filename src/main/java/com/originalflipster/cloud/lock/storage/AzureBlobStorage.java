package com.originalflipster.cloud.lock.storage;

import com.azure.core.util.BinaryData;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

final class AzureBlobStorage implements CloudStorage {

    private static final Logger LOG = LoggerFactory.getLogger(AzureBlobStorage.class);

    private final String containerName;
    private final String lockFile;
    private final String endpoint;
    private final DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();

    private Lock<Void> lock = null;

    private BlobServiceClient serviceClient;
    private BlobContainerClient containerClient;
    private BlobClient blobClient;

    AzureBlobStorage(final String containerName, final String lockFile, final String endpoint){
        this.containerName = Objects.requireNonNull(containerName);
        this.lockFile = Objects.requireNonNull(lockFile);
        this.endpoint = Objects.requireNonNull(endpoint);
    }

    private BlobServiceClient getServiceClient() {
        if (serviceClient == null) {
            serviceClient = new BlobServiceClientBuilder()
                    .endpoint(endpoint)
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
        } catch (BlobStorageException bse) {
            LOG.debug("failed to acquire lock - lock file already exists");
            return false;
        }
        lock = new Lock<>(null, LocalDateTime.now());
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
