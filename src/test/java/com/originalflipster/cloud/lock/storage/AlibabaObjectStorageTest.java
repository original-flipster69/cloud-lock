package com.originalflipster.cloud.lock.storage;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlibabaObjectStorageTest {

    private static final String BUCKET_NAME = "test-bucket";
    private static final String LOCK_FILE = "lock-file";

    private OSSClient ossClient;
    private AlibabaObjectStorage alibabaObjectStorage;

    @BeforeEach
    void setUp() {
        ossClient = mock(OSSClient.class);
        alibabaObjectStorage = new AlibabaObjectStorage(BUCKET_NAME, LOCK_FILE, "",  ossClient);
    }

    @Test
    void shouldReturnTrueWhenLockFileExists() {
        when(ossClient.doesObjectExist(BUCKET_NAME, LOCK_FILE)).thenReturn(true);
        assertTrue(alibabaObjectStorage.lockFileExists());
        verify(ossClient).doesObjectExist(BUCKET_NAME, LOCK_FILE);
    }

    @Test
    void shouldReturnFalseWhenLockFileDoesNotExist() {
        when(ossClient.doesObjectExist(BUCKET_NAME, LOCK_FILE)).thenReturn(false);
        assertFalse(alibabaObjectStorage.lockFileExists());
        verify(ossClient).doesObjectExist(BUCKET_NAME, LOCK_FILE);
    }

    @Test
    void shouldAcquireLockSuccessfully() {
        alibabaObjectStorage.lock();
        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(ossClient).putObject(any());
        assertTrue(alibabaObjectStorage.hasLock());
    }

    @Test
    void shouldFailToAcquireLock() {
        doThrow(new OSSException()).when(ossClient).putObject(any());
        assertFalse(alibabaObjectStorage.lock());
        assertFalse(alibabaObjectStorage.hasLock());
    }

    @Test
    void shouldGetLockContent() throws IOException {
        String expectedContent = "Lock content";
        OSSObject ossObject = mock(OSSObject.class);
        when(ossObject.getObjectContent()).thenReturn(new ByteArrayInputStream(expectedContent.getBytes()));
        when(ossClient.getObject(BUCKET_NAME, LOCK_FILE)).thenReturn(ossObject);

        String actualContent = alibabaObjectStorage.getLockContent();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void shouldDeleteLock() {
        alibabaObjectStorage.deleteLock();
        verify(ossClient).deleteObject(BUCKET_NAME, LOCK_FILE);
    }

    @Test
    void shouldUnlock() {
        alibabaObjectStorage.lock();
        assertTrue(alibabaObjectStorage.hasLock());

        alibabaObjectStorage.unlock();
        assertFalse(alibabaObjectStorage.hasLock());
    }
}