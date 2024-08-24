package com.originalflipster.cloud.lock.storage;

import java.time.LocalDateTime;

public final class Lock <T> {

    private final T blob;
    private final LocalDateTime timestamp;

    public Lock(final T blob, final LocalDateTime timestamp) {
        this.blob = blob;
        this.timestamp = timestamp;
    }

    public T getBlob() {
        return blob;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
