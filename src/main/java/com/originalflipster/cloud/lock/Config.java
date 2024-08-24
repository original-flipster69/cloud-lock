package com.originalflipster.cloud.lock;

public final class Config {

    private final String bucketName;
    private final String lockFile;
    private final String endpoint;
    private final String accessId;
    private final String accessKey;

    public Config(final String bucketName, final String lockFile, final String endpoint, final String accessId, final String accessKey) {
        this.bucketName = bucketName;
        this.lockFile = lockFile;
        this.endpoint = endpoint;
        this.accessId = accessId;
        this.accessKey = accessKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getLockFile() {
        return lockFile;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessId() {
        return accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }
}
