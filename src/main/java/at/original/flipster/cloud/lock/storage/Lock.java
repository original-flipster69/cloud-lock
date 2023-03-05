package at.original.flipster.cloud.lock.storage;

import java.time.LocalDateTime;

record Lock <T> (T blob, LocalDateTime timestamp) {
}
