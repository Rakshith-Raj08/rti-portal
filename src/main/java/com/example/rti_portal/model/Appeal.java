package com.example.rti_portal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appeals")
public class Appeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private Request request;

    @Column(nullable = false)
    private LocalDate filedAt;

    private String appellateAuthority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppealStatus status = AppealStatus.FILED;

    private LocalDate resolvedAt;

    public Appeal() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public LocalDate getFiledAt() { return filedAt; }
    public void setFiledAt(LocalDate filedAt) { this.filedAt = filedAt; }

    public String getAppellateAuthority() { return appellateAuthority; }
    public void setAppellateAuthority(String appellateAuthority) { this.appellateAuthority = appellateAuthority; }

    public AppealStatus getStatus() { return status; }
    public void setStatus(AppealStatus status) { this.status = status; }

    public LocalDate getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDate resolvedAt) { this.resolvedAt = resolvedAt; }
}
