package com.perfectahr.customer_support_hub.customer.service;

import com.perfectahr.customer_support_hub.customer.dto.CreateCustomerRequest;
import com.perfectahr.customer_support_hub.customer.dto.CustomerResponse;
import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.DuplicateResourceException;
import com.perfectahr.customer_support_hub.exception.NotFoundException;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerResponse createCustomer(Long agentId, CreateCustomerRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found"));

        User customer = new User();
        customer.setUsername(request.getUsername());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setRole(Role.CUSTOMER);
        customer.setAgent(agent);

        User saved = userRepository.save(customer);

        return mapToResponse(saved);
    }

    @Override
    public List<CustomerResponse> getCustomersByAgent(Long agentId) {
        return userRepository.findAllByAgentIdAndRole(agentId, Role.CUSTOMER)
                .stream()
                .map(this::mapToResponse)
                .toList();
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
