package com.perfectahr.customer_support_hub.ticket.controller;

import com.perfectahr.customer_support_hub.ticket.dto.CreateTicketRequest;
import com.perfectahr.customer_support_hub.ticket.dto.TicketResponse;
import com.perfectahr.customer_support_hub.ticket.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping("/my")
    public List<TicketResponse> getMyTickets() {
        return ticketService.getMyTickets();
    }

    @GetMapping("/customers")
    public List<TicketResponse> getMyCustomersTickets() {
        return ticketService.getMyCustomersTickets();
    }
}