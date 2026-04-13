package com.perfectahr.customer_support_hub.ticket.service;

import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.Ticket;
import com.perfectahr.customer_support_hub.entity.TicketStatus;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.repository.TicketRepository;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import com.perfectahr.customer_support_hub.ticket.dto.CreateTicketRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private User customer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        customer = new User();
        customer.setUsername("cust1");
        customer.setRole(Role.CUSTOMER);

        // ✔️ Set ID using reflection (correct for JPA entity)
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(customer, 1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("cust1", null)
        );

        when(userRepository.findByUsername("cust1"))
                .thenReturn(Optional.of(customer));
    }

    @Test
    void createTicket_success() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setSubject("test");
        request.setDescription("desc");

        Ticket saved = new Ticket();
        saved.setCustomer(customer);
        saved.setSubject("test");

        when(ticketRepository.save(any())).thenReturn(saved);

        var response = ticketService.createTicket(request);

        assertThat(response.getSubject()).isEqualTo("test");
        verify(ticketRepository).save(any());
    }

    @Test
    void createTicket_shouldFail_whenNotCustomer() {
        customer.setRole(Role.AGENT);

        CreateTicketRequest request = new CreateTicketRequest();
        request.setSubject("test");
        request.setDescription("desc");

        assertThatThrownBy(() -> ticketService.createTicket(request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getMyTickets_success() {
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);

        when(ticketRepository.findAllByCustomerId(1L))
                .thenReturn(List.of(ticket));

        var result = ticketService.getMyTickets();

        assertThat(result).hasSize(1);
    }

    @Test
    void getMyTickets_shouldFail_whenNotCustomer() {
        customer.setRole(Role.ADMIN);

        assertThatThrownBy(() -> ticketService.getMyTickets())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getMyCustomersTickets_asAgent() throws Exception {
        customer.setRole(Role.AGENT);

        User agentCustomer = new User();
        agentCustomer.setUsername("real-customer");
        agentCustomer.setRole(Role.CUSTOMER);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(agentCustomer, 2L);

        Ticket ticket = new Ticket();
        ticket.setCustomer(agentCustomer);
        ticket.setSubject("agent ticket");
        ticket.setDescription("agent desc");
        ticket.setStatus(TicketStatus.OPEN);

        when(ticketRepository.findAllByCustomerAgentId(1L))
                .thenReturn(List.of(ticket));

        var result = ticketService.getMyCustomersTickets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(2L);
        assertThat(result.get(0).getSubject()).isEqualTo("agent ticket");
    }
}