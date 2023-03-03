package at.original.flipster.cloud.lock;

import com.google.cloud.storage.BlobId;

import java.time.LocalDateTime;

record Lock(BlobId blob, LocalDateTime timestamp) {
}
