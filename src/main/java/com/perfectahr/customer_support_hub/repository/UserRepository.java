package com.perfectahr.customer_support_hub.repository;

import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findAllByAgentId(Long agentId);

    List<User> findAllByAgentIdAndRole(Long agentId, Role role);

    Optional<User> findByIdAndAgentId(Long id, Long agentId);
}
