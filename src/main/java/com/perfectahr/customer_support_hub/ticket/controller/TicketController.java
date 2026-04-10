package com.perfectahr.customer_support_hub.ticket.controller;

import com.perfectahr.customer_support_hub.ticket.dto.CreateTicketRequest;
import com.perfectahr.customer_support_hub.ticket.dto.TicketResponse;
import com.perfectahr.customer_support_hub.ticket.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/customers/{customerId}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@PathVariable Long customerId,
                                       @Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(customerId, request);
    }

    @GetMapping("/customers/{customerId}/tickets")
    public List<TicketResponse> getTicketsByCustomer(@PathVariable Long customerId) {
        return ticketService.getTicketsByCustomer(customerId);
    }

    @GetMapping("/agents/{agentId}/tickets")
    public List<TicketResponse> getTicketsByAgent(@PathVariable Long agentId) {
        return ticketService.getTicketsByAgent(agentId);
    }
}