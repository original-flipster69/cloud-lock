package com.originalflipster.cloud.lock.storage;

import com.originalflipster.cloud.lock.Config;

import java.util.Objects;

public final class StorageFactory {

    private final Config config;

    public StorageFactory(final Config config) {
        this.config = Objects.requireNonNull(config);
    }

    public CloudStorage storage(final Vendor vendor) {
        switch (vendor) {
            case GCP:
                return new GoogleCloudStorage(config.getBucketName(), config.getLockFile());
            case AZURE:
                return new AzureBlobStorage(config.getBucketName().replaceAll("-", ""), config.getLockFile(), config.getEndpoint());
            case ALIBABA:
                return new AlibabaObjectStorage(config.getBucketName(), config.getLockFile(), config.getEndpoint(), config.getAccessId(), config.getAccessKey());
        }
        throw new IllegalArgumentException("no storage implementation found for vendor = " + vendor);
    }
}
