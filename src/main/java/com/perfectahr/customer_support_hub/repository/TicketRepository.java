package com.perfectahr.customer_support_hub.repository;

import com.perfectahr.customer_support_hub.entity.Ticket;
import com.perfectahr.customer_support_hub.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByCustomerId(Long customerId);

    Optional<Ticket> findByIdAndCustomerId(Long id, Long customerId);

    List<Ticket> findAllByCustomerAgentId(Long agentId);

    List<Ticket> findAllByCustomerAgentIdAndStatus(Long agentId, TicketStatus status);

    Optional<Ticket> findByIdAndCustomerAgentId(Long id, Long agentId);
}
