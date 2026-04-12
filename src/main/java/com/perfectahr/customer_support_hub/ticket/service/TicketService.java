package com.perfectahr.customer_support_hub.ticket.service;

import com.perfectahr.customer_support_hub.ticket.dto.CreateTicketRequest;
import com.perfectahr.customer_support_hub.ticket.dto.TicketResponse;

import java.util.List;

public interface TicketService {

    TicketResponse createTicket(CreateTicketRequest request);

    List<TicketResponse> getMyTickets();

    List<TicketResponse> getMyCustomersTickets();
}