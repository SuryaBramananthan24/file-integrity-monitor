package com.devops.file_integrity_monitor.persistence;

import com.devops.file_integrity_monitor.integrity.IntegrityBaseline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Instant;
import java.util.Optional;

@SpringBootTest
public class IntegrityBaselinePostgresTest
        extends AbstractTestNGSpringContextTests {

    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("file_integrity_test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );

        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );
    }

    @Autowired
    private IntegrityBaselineService baselineService;

    @Test
    public void shouldPersistAndRetrieveBaseline() {

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertNotNull(
                baselineService,
                "IntegrityBaselineService should be injected by Spring"
        );

        Instant createdAt = Instant.now();

        IntegrityBaseline baseline =
                new IntegrityBaseline(
                        "/integration-test/file.txt",
                        "test-agent",
                        "SHA-256",
                        "abc123",
                        createdAt
                );

        baselineService.save(baseline);

        Optional<IntegrityBaseline> result =
                baselineService.find(
                        "test-agent",
                        "/integration-test/file.txt"
                );

        softAssert.assertTrue(
                result.isPresent(),
                "Baseline should be present after saving"
        );

        if (result.isPresent()) {

            IntegrityBaseline savedBaseline = result.get();

            softAssert.assertEquals(
                    savedBaseline.digest(),
                    "abc123",
                    "Digest should match"
            );

            softAssert.assertEquals(
                    savedBaseline.algorithm(),
                    "SHA-256",
                    "Algorithm should match"
            );

            softAssert.assertEquals(
                    savedBaseline.sourceId(),
                    "test-agent",
                    "Source ID should match"
            );

            softAssert.assertEquals(
                    savedBaseline.resourceId(),
                    "/integration-test/file.txt",
                    "Resource ID should match"
            );
        }

        softAssert.assertAll();
    }

    @Test
    public void shouldUpdateExistingBaseline() {

        SoftAssert softAssert = new SoftAssert();

        Instant createdAt = Instant.now();

        IntegrityBaseline original =
                new IntegrityBaseline(
                        "/integration-test/update.txt",
                        "test-agent",
                        "SHA-256",
                        "old-digest",
                        createdAt
                );

        baselineService.save(original);

        IntegrityBaseline updated =
                new IntegrityBaseline(
                        "/integration-test/update.txt",
                        "test-agent",
                        "SHA-256",
                        "new-digest",
                        createdAt
                );

        baselineService.save(updated);

        Optional<IntegrityBaseline> result =
                baselineService.find(
                        "test-agent",
                        "/integration-test/update.txt"
                );

        softAssert.assertTrue(
                result.isPresent(),
                "Updated baseline should exist"
        );

        if (result.isPresent()) {

            softAssert.assertEquals(
                    result.get().digest(),
                    "new-digest",
                    "Digest should be updated"
            );

            softAssert.assertEquals(
                    result.get().sourceId(),
                    "test-agent",
                    "Source ID should remain unchanged"
            );

            softAssert.assertEquals(
                    result.get().resourceId(),
                    "/integration-test/update.txt",
                    "Resource ID should remain unchanged"
            );
        }

        softAssert.assertAll();
    }


    @Test
    public void shouldKeepBaselinesForDifferentSources() {

        SoftAssert softAssert = new SoftAssert();

        Instant createdAt = Instant.now();

        IntegrityBaseline agentOne =
                new IntegrityBaseline(
                        "/same/path/config.yml",
                        "agent-one",
                        "SHA-256",
                        "digest-one",
                        createdAt
                );

        IntegrityBaseline agentTwo =
                new IntegrityBaseline(
                        "/same/path/config.yml",
                        "agent-two",
                        "SHA-256",
                        "digest-two",
                        createdAt
                );

        baselineService.save(agentOne);
        baselineService.save(agentTwo);

        Optional<IntegrityBaseline> resultOne =
                baselineService.find(
                        "agent-one",
                        "/same/path/config.yml"
                );

        Optional<IntegrityBaseline> resultTwo =
                baselineService.find(
                        "agent-two",
                        "/same/path/config.yml"
                );

        softAssert.assertTrue(
                resultOne.isPresent(),
                "Agent one baseline should exist"
        );

        softAssert.assertTrue(
                resultTwo.isPresent(),
                "Agent two baseline should exist"
        );

        if (resultOne.isPresent() && resultTwo.isPresent()) {

            softAssert.assertEquals(
                    resultOne.get().digest(),
                    "digest-one"
            );

            softAssert.assertEquals(
                    resultTwo.get().digest(),
                    "digest-two"
            );
        }

        softAssert.assertAll();
    }

    @AfterClass(alwaysRun = true)
    public void stopContainer() {
        POSTGRES.stop();
    }
}