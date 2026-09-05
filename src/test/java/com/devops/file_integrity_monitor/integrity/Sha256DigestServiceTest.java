package com.devops.file_integrity_monitor.integrity;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.nio.file.Files;
import java.nio.file.Path;
//import static org.testng.Assert.assertEquals;

public class Sha256DigestServiceTest {
    private final Sha256DigestService digestService = new Sha256DigestService();
    private SoftAssert softAssert = new SoftAssert();
    @Test
    public void shouldCalculateKnownSha256Digest() throws Exception {
        Path file = Files.createTempFile("fim-test-", ".txt");
        try {
            Files.writeString(file, "hello world");
            String digest = digestService.calculate(file);
            softAssert.assertEquals(digest,"b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9");
            softAssert.assertAll();
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void shouldCalculateDigestForEmptyFile() throws Exception {
        Path file = Files.createTempFile("fim-empty-", ".txt");
        try {
            String digest = digestService.calculate(file);
            softAssert.assertEquals(digest,"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
            softAssert.assertAll();
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void shouldProduceSameDigestForSameContent() throws Exception {

        Path firstFile = Files.createTempFile("fim-first-", ".txt");
        Path secondFile = Files.createTempFile("fim-second-", ".txt");

        try {
            Files.writeString(firstFile, "integrity monitoring");
            Files.writeString(secondFile, "integrity monitoring");
            String firstDigest = digestService.calculate(firstFile);
            String secondDigest = digestService.calculate(secondFile);
            softAssert.assertEquals(firstDigest, secondDigest);
            softAssert.assertAll();
        }
        finally {
            Files.deleteIfExists(firstFile);
            Files.deleteIfExists(secondFile);
        }
    }
}