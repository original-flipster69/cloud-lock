package at.original.flipster.cloud.lock.storage;

import java.util.Objects;

public final class StorageFactory {

    //FIXME put in config object?
    private final String bucketName;
    private final String lockFile;

    public StorageFactory(final String bucketName, final String lockFile) {
        this.bucketName = Objects.requireNonNull(bucketName);
        this.lockFile = Objects.requireNonNull(lockFile);
    }

    public CloudStorage storage(final Vendor vendor) {
        switch (vendor) {
            case GCP:
                return new GoogleCloudStorage(bucketName, lockFile);
            case AZURE:
                return new AzureBlobStorage(bucketName.replaceAll("-", ""), lockFile);
        }
        throw new IllegalArgumentException("no storage implementation found for vendor = " + vendor);
    }
}
