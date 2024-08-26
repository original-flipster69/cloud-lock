package com.originalflipster.cloud.lock;

import com.originalflipster.cloud.lock.storage.CloudStorage;

import static org.junit.jupiter.api.Assertions.*;

class StorageLockTest {


    private static final class MockStorage implements CloudStorage {

        @Override
        public boolean lockFileExists() {
            return false;
        }

        @Override
        public boolean lock() {
            return false;
        }

        @Override
        public String getLockContent() {
            return null;
        }

        @Override
        public boolean hasLock() {
            return false;
        }

        @Override
        public void deleteLock() {

        }

        @Override
        public void unlock() {

        }
    }
}