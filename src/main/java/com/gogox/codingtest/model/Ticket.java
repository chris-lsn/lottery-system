package com.gogox.codingtest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Ticket {
    private UUID ticketId;

    private LocalDateTime createdAt;

    private String username;

    public Ticket(String username)
    {
        this.ticketId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.username = username;
    }
}
