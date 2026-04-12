package com.perfectahr.customer_support_hub.ticket.service;

import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.Ticket;
import com.perfectahr.customer_support_hub.entity.TicketStatus;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.NotFoundException;
import com.perfectahr.customer_support_hub.repository.TicketRepository;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import com.perfectahr.customer_support_hub.ticket.dto.CreateTicketRequest;
import com.perfectahr.customer_support_hub.ticket.dto.TicketResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TicketResponse createTicket(CreateTicketRequest request) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.CUSTOMER) {
            throw new AccessDeniedException("Only CUSTOMER can create tickets");
        }

        Ticket ticket = new Ticket();
        ticket.setCustomer(currentUser);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);

        Ticket saved = ticketRepository.save(ticket);

        return mapToResponse(saved);
    }

    @Override
    public List<TicketResponse> getMyTickets() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.CUSTOMER) {
            throw new AccessDeniedException("Only CUSTOMER can view their tickets");
        }

        return ticketRepository.findAllByCustomerId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TicketResponse> getMyCustomersTickets() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.AGENT && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only AGENT or ADMIN can view customer tickets");
        }

        if (currentUser.getRole() == Role.ADMIN) {
            return ticketRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return ticketRepository.findAllByCustomerAgentId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
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