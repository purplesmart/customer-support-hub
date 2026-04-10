package com.perfectahr.customer_support_hub.ticket.service;

import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.Ticket;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.NotFoundException;
import com.perfectahr.customer_support_hub.repository.TicketRepository;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import com.perfectahr.customer_support_hub.ticket.dto.CreateTicketRequest;
import com.perfectahr.customer_support_hub.ticket.dto.TicketResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TicketResponse createTicket(Long customerId, CreateTicketRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new NotFoundException("Customer not found");
        }

        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }

    @Override
    public List<TicketResponse> getTicketsByCustomer(Long customerId) {
        return ticketRepository.findAllByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TicketResponse> getTicketsByAgent(Long agentId) {
        return ticketRepository.findAllByCustomerAgentId(agentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getCustomer().getId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}