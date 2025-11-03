package com.digitalbank.customerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_customers_email", columnNames = "email"),
                @UniqueConstraint(name = "uniq_customers_external_id", columnNames = "external_id")
        })
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version; // optimistic concurrency

    @Column(name = "first_name", nullable = false) private String firstName;
    @Column(name = "last_name", nullable = false) private String lastName;
    @Column(name = "email", nullable = false) private String email;
    @Column(name = "phone") private String phone;
    @Column(name = "address", nullable = false) private String address;

    @Column(name = "external_id", nullable = false)
    private String externalId; //treat as idempotency key for create

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    private KycStatus kycStatus;

    @Column(name = "active", nullable = false) private Boolean active;

    @Column(name = "request_fingerprint", nullable = false)
    private  String requestFingerprint;

    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
