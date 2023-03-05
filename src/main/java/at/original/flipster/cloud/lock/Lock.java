package at.original.flipster.cloud.lock;

import java.time.LocalDateTime;

record  Lock <T> (T blob, LocalDateTime timestamp) {
}
