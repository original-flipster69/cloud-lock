package com.originalflipster.cloud.lock;

public record Config(String bucketName, String lockFile, String endpoint, String accessId, String accessKey) {
}
