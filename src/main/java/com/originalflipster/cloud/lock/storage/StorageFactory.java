package com.originalflipster.cloud.lock.storage;

import com.originalflipster.cloud.lock.Config;
import com.originalflipster.cloud.lock.Vendor;

import java.util.Objects;

public final class StorageFactory {

    private final Config config;
    private final Vendor vendor;

    public StorageFactory(final Vendor vendor, final Config config) {
        this.vendor = Objects.requireNonNull(vendor);
        this.config = Objects.requireNonNull(config);
    }

    public CloudStorage storage() {
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
