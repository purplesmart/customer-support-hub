package com.perfectahr.customer_support_hub.customer.service;

import com.perfectahr.customer_support_hub.customer.dto.CreateCustomerRequest;
import com.perfectahr.customer_support_hub.customer.dto.CustomerResponse;
import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.DuplicateResourceException;
import com.perfectahr.customer_support_hub.exception.NotFoundException;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.AGENT && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only AGENT or ADMIN can create customers");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User customer = new User();
        customer.setUsername(request.username());
        customer.setPassword(passwordEncoder.encode(request.password()));
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setRole(Role.CUSTOMER);

        if (currentUser.getRole() == Role.AGENT) {
            customer.setAgent(currentUser);
        }

        User saved = userRepository.save(customer);

        return mapToResponse(saved);
    }

    @Override
    public List<CustomerResponse> getMyCustomers() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return userRepository.findAll().stream()
                    .filter(user -> user.getRole() == Role.CUSTOMER)
                    .map(this::mapToResponse)
                    .toList();
        }

        if (currentUser.getRole() == Role.AGENT) {
            return userRepository.findAllByAgentId(currentUser.getId()).stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        throw new AccessDeniedException("Only AGENT or ADMIN can view customers");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
    }

    private CustomerResponse mapToResponse(User user) {
        return new CustomerResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}