package com.originalflipster.cloud.lock.storage;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

final class AlibabaObjectStorage implements CloudStorage {

    private static final Logger LOG = LoggerFactory.getLogger(AlibabaObjectStorage.class);

    private final String bucketName;
    private final String lockFile;
    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;

    private boolean lock = false;
    private OSS ossClient;

    AlibabaObjectStorage(final String bucketName, final String lockFile, final String endpoint, final String accessKeyId, final String accessKeySecret) {
        this.bucketName = bucketName;
        this.lockFile = lockFile;
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    private OSS getClient() {
        if (ossClient == null) {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }
        return ossClient;
    }

    @Override
    public boolean lockFileExists() {
        return getClient().doesObjectExist(bucketName, lockFile);
    }

    @Override
    public boolean lock() {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, lockFile, new ByteArrayInputStream(LocalDateTime.now().toString().getBytes()));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader("x-oss-forbid-overwrite", "true");
            putObjectRequest.setMetadata(metadata);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            LOG.debug("failed to acquire lock - lock file already exists");
            return false;
        } catch (ClientException ce) {
            LOG.error("failed to send putobjectrequest", ce);
            return false;
        }
        lock = true;
        return true;
    }

    @Override
    public String getLockContent() {
        try {
            return new String(getClient().getObject(bucketName, lockFile).getObjectContent().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("failed reading content from lock file", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasLock() {
        return lock;
    }

    @Override
    public void deleteLock() {
        getClient().deleteObject(bucketName, lockFile);
    }

    @Override
    public void unlock() {
        lock = false;
    }
}
