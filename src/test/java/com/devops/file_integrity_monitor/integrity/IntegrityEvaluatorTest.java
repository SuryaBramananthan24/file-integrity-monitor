package com.devops.file_integrity_monitor.integrity;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;


public class IntegrityEvaluatorTest {

    private final DigestService digestService = new Sha256DigestService();
    private final IntegrityEvaluator evaluator = new IntegrityEvaluator(digestService);
    private SoftAssert softAssert = new SoftAssert();

    @Test
    public void shouldReportUnchangedWhenDigestMatches() throws Exception {
        Path file = Files.createTempFile("fim-", ".txt");

        try {
            Files.writeString(file, "original content");
            String digest = digestService.calculate(file);

            IntegrityBaseline baseline = new IntegrityBaseline(file.toAbsolutePath().normalize().toString(),"filesystem-agent-01","SHA-256",digest,Instant.now());
            IntegrityResult result = evaluator.evaluate(file, baseline);

            softAssert.assertEquals(result.status(),IntegrityStatus.UNCHANGED);
            softAssert.assertEquals(result.currentDigest(),digest);

        }
        finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void shouldReportChangedWhenDigestDiffers()
            throws Exception {

        Path file = Files.createTempFile("fim-", ".txt");

        try {
            Files.writeString(file, "original content");

            String originalDigest = digestService.calculate(file);
            IntegrityBaseline baseline = new IntegrityBaseline(file.toAbsolutePath().normalize().toString(),"filesystem-agent-01","SHA-256",originalDigest,Instant.now());

            Files.writeString(file, "modified content");
            IntegrityResult result = evaluator.evaluate(file, baseline);
            softAssert.assertEquals(result.status(),IntegrityStatus.CHANGED);
            softAssert.assertEquals(result.previousDigest(),originalDigest);

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void shouldReportUnavailableWhenFileDoesNotExist() {
        Path file = Path.of("target","non-existent-integrity-test-file.txt");

        IntegrityBaseline baseline = new IntegrityBaseline(file.toAbsolutePath().normalize().toString(),"filesystem-agent-01", "SHA-256","some-baseline-digest",Instant.now());
        IntegrityResult result = evaluator.evaluate(file, baseline);
        softAssert.assertEquals(result.status(),IntegrityStatus.UNAVAILABLE);
    }
}