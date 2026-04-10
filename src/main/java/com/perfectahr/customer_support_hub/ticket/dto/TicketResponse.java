package com.perfectahr.customer_support_hub.ticket.dto;

import com.perfectahr.customer_support_hub.entity.TicketStatus;
import java.time.Instant;

public class TicketResponse {

    private final Long id;
    private final Long customerId;
    private final String subject;
    private final String description;
    private final TicketStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public TicketResponse(Long id,
                          Long customerId,
                          String subject,
                          String description,
                          TicketStatus status,
                          Instant createdAt,
                          Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
