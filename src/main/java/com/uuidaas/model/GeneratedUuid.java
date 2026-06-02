package com.uuidaas.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "generated_uuids")
public class GeneratedUuid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String status;

    public GeneratedUuid() {}

    public GeneratedUuid(String uuid, String requestedBy) {
        this.uuid = uuid;
        this.requestedBy = requestedBy;
        this.createdAt = Instant.now();
        this.status = "ACTIVE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
