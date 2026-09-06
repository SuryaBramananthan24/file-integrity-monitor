package com.devops.file_integrity_monitor.service;

import com.devops.file_integrity_monitor.integrity.DigestService;
import com.devops.file_integrity_monitor.integrity.IntegrityBaseline;
import com.devops.file_integrity_monitor.integrity.IntegrityEvaluator;
import com.devops.file_integrity_monitor.persistence.IntegrityBaselineService;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.devops.file_integrity_monitor.persistence.IntegrityAuditService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IntegrityMonitoringServiceTest{

    @Test
    public void shouldCreateAndPersistBaseline() throws IOException {
        SoftAssert softAssert = new SoftAssert();
        Path tempFile = Files.createTempFile("integrity-test",".txt");
        Files.writeString(tempFile,"test content");
        IntegrityAuditService auditService = mock(IntegrityAuditService.class);
        IntegrityBaselineService baselineService = mock(IntegrityBaselineService.class);
        IntegrityEvaluator integrityEvaluator = mock(IntegrityEvaluator.class);
        DigestService digestService = mock(DigestService.class);

        String digest = "test-digest";
        when(digestService.calculate(tempFile)).thenReturn(digest);

        IntegrityBaseline expectedBaseline = new IntegrityBaseline(tempFile.toAbsolutePath().normalize().toString(),"filesystem-local","SHA-256",digest, Instant.now());
        when(baselineService.save(any(IntegrityBaseline.class))).thenReturn(expectedBaseline);
        IntegrityMonitoringService service = new IntegrityMonitoringService(baselineService,auditService,integrityEvaluator,digestService);
        IntegrityBaseline result = service.createBaseline(tempFile.toString());

        softAssert.assertNotNull(result,"Baseline should be created");
        softAssert.assertEquals(result.digest(),digest,"Digest should match");
        softAssert.assertEquals(result.sourceId(),"filesystem-local","Source ID should match");
        softAssert.assertEquals(result.algorithm(),"SHA-256","Algorithm should match");
        verify(baselineService).save(any(IntegrityBaseline.class));
        Files.deleteIfExists(tempFile);
        softAssert.assertAll();
    }
}