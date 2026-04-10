package com.perfectahr.customer_support_hub.ticket.service;

import com.perfectahr.customer_support_hub.ticket.dto.CreateTicketRequest;
import com.perfectahr.customer_support_hub.ticket.dto.TicketResponse;
import java.util.List;

public interface TicketService {

    TicketResponse createTicket(Long customerId, CreateTicketRequest request);

    List<TicketResponse> getTicketsByCustomer(Long customerId);

    List<TicketResponse> getTicketsByAgent(Long agentId);
}
