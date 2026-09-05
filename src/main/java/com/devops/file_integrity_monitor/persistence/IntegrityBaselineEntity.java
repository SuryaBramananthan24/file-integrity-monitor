package com.devops.file_integrity_monitor.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "integrity_baselines",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_baseline_resource",
                        columnNames = {"source_id", "resource_id"}
                )
        }
)
public class IntegrityBaselineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false, length = 255)
    private String sourceId;

    @Column(name = "resource_id", nullable = false, length = 1000)
    private String resourceId;

    @Column(nullable = false, length = 50)
    private String algorithm;

    @Column(nullable = false, length = 128)
    private String digest;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected IntegrityBaselineEntity() {    }

    public IntegrityBaselineEntity(
            String sourceId,
            String resourceId,
            String algorithm,
            String digest,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.sourceId = sourceId;
        this.resourceId = resourceId;
        this.algorithm = algorithm;
        this.digest = digest;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getDigest() {
        return digest;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateDigest(String digest, Instant updatedAt) {
        this.digest = digest;
        this.updatedAt = updatedAt;
    }
}