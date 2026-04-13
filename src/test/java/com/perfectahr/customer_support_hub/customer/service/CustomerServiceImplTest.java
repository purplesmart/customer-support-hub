package com.perfectahr.customer_support_hub.customer.service;

import com.perfectahr.customer_support_hub.customer.dto.CreateCustomerRequest;
import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.DuplicateResourceException;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private User currentUser;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        currentUser = new User();
        currentUser.setUsername("agent1");
        currentUser.setRole(Role.AGENT);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(currentUser, 1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("agent1", null)
        );

        when(userRepository.findByUsername("agent1"))
                .thenReturn(Optional.of(currentUser));
    }

    @Test
    void createCustomer_success() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "cust1",
                "password123",
                "John",
                "Doe",
                "john@example.com"
        );

        when(userRepository.existsByUsername("cust1")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        User savedCustomer = new User();
        savedCustomer.setUsername("cust1");
        savedCustomer.setFirstName("John");
        savedCustomer.setLastName("Doe");
        savedCustomer.setEmail("john@example.com");
        savedCustomer.setRole(Role.CUSTOMER);
        savedCustomer.setAgent(currentUser);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedCustomer, 2L);

        when(userRepository.save(any(User.class))).thenReturn(savedCustomer);

        var response = customerService.createCustomer(request);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getUsername()).isEqualTo("cust1");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createCustomer_shouldFail_whenCurrentUserIsCustomer() {
        currentUser.setRole(Role.CUSTOMER);

        CreateCustomerRequest request = new CreateCustomerRequest(
                "cust1",
                "password123",
                "John",
                "Doe",
                "john@example.com"
        );

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only AGENT or ADMIN can create customers");
    }

    @Test
    void createCustomer_shouldFail_whenUsernameAlreadyExists() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "cust1",
                "password123",
                "John",
                "Doe",
                "john@example.com"
        );

        when(userRepository.existsByUsername("cust1")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void createCustomer_shouldFail_whenEmailAlreadyExists() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "cust1",
                "password123",
                "John",
                "Doe",
                "john@example.com"
        );

        when(userRepository.existsByUsername("cust1")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");
    }

    @Test
    void getMyCustomers_asAgent_success() throws Exception {
        User customer = new User();
        customer.setUsername("cust1");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setRole(Role.CUSTOMER);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(customer, 2L);

        when(userRepository.findAllByAgentId(1L)).thenReturn(List.of(customer));

        var result = customerService.getMyCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(0).getUsername()).isEqualTo("cust1");
    }

    @Test
    void getMyCustomers_shouldFail_whenCurrentUserIsCustomer() {
        currentUser.setRole(Role.CUSTOMER);

        assertThatThrownBy(() -> customerService.getMyCustomers())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only AGENT or ADMIN can view customers");
    }

    @Test
    void getMyCustomers_asAdmin_returnsOnlyCustomers() throws Exception {
        currentUser.setRole(Role.ADMIN);

        User customer = new User();
        customer.setUsername("cust1");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setRole(Role.CUSTOMER);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(customer, 2L);

        User agent = new User();
        agent.setUsername("agent2");
        agent.setRole(Role.AGENT);

        when(userRepository.findAll()).thenReturn(List.of(customer, agent));

        var result = customerService.getMyCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("cust1");
    }
}