package com.devops.file_integrity_monitor.persistence;

import com.devops.file_integrity_monitor.integrity.IntegrityBaseline;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.time.Instant;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class IntegrityBaselineServiceTest {
    @Mock
    private IntegrityBaselineRepository repository;
    private IntegrityBaselineService service;
    private AutoCloseable mocks;
    private SoftAssert softAssert = new SoftAssert();
    @BeforeMethod
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new IntegrityBaselineService(repository);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void shouldReturnBaselineWhenItExists() {
        Instant createdAt = Instant.now();
        IntegrityBaselineEntity entity = new IntegrityBaselineEntity("filesystem-local", "/tmp/test.txt","SHA-256","abc123",createdAt,createdAt);
        when(repository.findBySourceIdAndResourceId("filesystem-local","/tmp/test.txt")).thenReturn(Optional.of(entity));
        Optional<IntegrityBaseline> result = service.find("filesystem-local","/tmp/test.txt");
        softAssert.assertTrue(result.isPresent());
        softAssert.assertEquals(result.get().digest(), "abc123");
        softAssert.assertEquals(result.get().algorithm(), "SHA-256");
    }

    @Test
    public void shouldReturnEmptyWhenBaselineDoesNotExist() {
        when(repository.findBySourceIdAndResourceId("filesystem-local","/tmp/missing.txt")).thenReturn(Optional.empty());
        Optional<IntegrityBaseline> result = service.find("filesystem-local","/tmp/missing.txt");
        softAssert.assertFalse(result.isPresent());
    }

    @Test
    public void shouldCreateNewBaseline() {
        Instant createdAt = Instant.now();
        IntegrityBaseline baseline = new IntegrityBaseline("/tmp/test.txt","filesystem-local","SHA-256","abc123",createdAt);

        when(repository.findBySourceIdAndResourceId("filesystem-local","/tmp/test.txt")).thenReturn(Optional.empty());
        when(repository.save(any(IntegrityBaselineEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntegrityBaseline result = service.save(baseline);

        softAssert.assertEquals(result.resourceId(), "/tmp/test.txt");
        softAssert.assertEquals(result.sourceId(), "filesystem-local");
        softAssert.assertEquals(result.algorithm(), "SHA-256");
        softAssert.assertEquals(result.digest(), "abc123");

        verify(repository).save(any(IntegrityBaselineEntity.class));
    }

    @Test
    public void shouldUpdateExistingBaseline() {
        Instant createdAt = Instant.now();
        IntegrityBaselineEntity existing = new IntegrityBaselineEntity("filesystem-local","/tmp/test.txt","SHA-256", "old-digest",createdAt,createdAt);
        IntegrityBaseline updated = new IntegrityBaseline("/tmp/test.txt","filesystem-local","SHA-256","new-digest",createdAt);

        when(repository.findBySourceIdAndResourceId(eq("filesystem-local"),eq("/tmp/test.txt"))).thenReturn(Optional.of(existing));
        when(repository.save(any(IntegrityBaselineEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntegrityBaseline result = service.save(updated);
        assertEquals(result.digest(), "new-digest");
        verify(repository).save(existing);
    }
}