package at.original.flipster.cloud.lock.storage;

import at.original.flipster.cloud.lock.Config;

import java.util.Objects;

public final class StorageFactory {

    private final Config config;

    public StorageFactory(final Config config) {
        this.config = Objects.requireNonNull(config);
    }

    public CloudStorage storage(final Vendor vendor) {
        switch (vendor) {
            case GCP:
                return new GoogleCloudStorage(config.bucketName(), config.lockFile());
            case AZURE:
                return new AzureBlobStorage(config.bucketName().replaceAll("-", ""), config.lockFile(), config.endpoint());
            case ALIBABA:
                return new AlibabaObjectStorage(config.bucketName(), config.lockFile(), config.endpoint(), config.accessId(), config.accessKey());
        }
        throw new IllegalArgumentException("no storage implementation found for vendor = " + vendor);
    }
}
