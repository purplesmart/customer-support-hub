package com.perfectahr.customer_support_hub.user.service;

import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.DuplicateResourceException;
import com.perfectahr.customer_support_hub.exception.NotFoundException;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import com.perfectahr.customer_support_hub.user.dto.CreateUserRequest;
import com.perfectahr.customer_support_hub.user.dto.UpdateMyProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.lang.reflect.Field;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "agent1",
                "password123",
                "John",
                "Doe",
                "john@example.com",
                Role.AGENT
        );

        when(userRepository.existsByUsername("agent1")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setUsername("agent1");
        savedUser.setPassword("encoded-password");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setRole(Role.AGENT);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedUser, 10L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var response = userService.createUser(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.username()).isEqualTo("agent1");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.role()).isEqualTo(Role.AGENT);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldFail_whenUsernameAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "agent1",
                "password123",
                "John",
                "Doe",
                "john@example.com",
                Role.AGENT
        );

        when(userRepository.existsByUsername("agent1")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void createUser_shouldFail_whenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "agent1",
                "password123",
                "John",
                "Doe",
                "john@example.com",
                Role.AGENT
        );

        when(userRepository.existsByUsername("agent1")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");
    }

    @Test
    void updateMyProfile_success() throws Exception {
        UpdateMyProfileRequest request = new UpdateMyProfileRequest(
                "David",
                "Ilous",
                "david@example.com"
        );

        User currentUser = new User();
        currentUser.setUsername("david");
        currentUser.setFirstName("Old");
        currentUser.setLastName("Name");
        currentUser.setEmail("old@example.com");
        currentUser.setRole(Role.CUSTOMER);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(currentUser, 7L);

        when(userRepository.findByUsername("david")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        var response = userService.updateMyProfile("david", request);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.firstName()).isEqualTo("David");
        assertThat(response.lastName()).isEqualTo("Ilous");
        assertThat(response.email()).isEqualTo("david@example.com");
        assertThat(response.role()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void updateMyProfile_shouldFail_whenUserNotFound() {
        UpdateMyProfileRequest request = new UpdateMyProfileRequest(
                "David",
                "Ilous",
                "david@example.com"
        );

        when(userRepository.findByUsername("missing-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateMyProfile("missing-user", request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Authenticated user not found");
    }

    @Test
    void updateMyProfile_shouldFail_whenEmailBelongsToAnotherUser() throws Exception {
        UpdateMyProfileRequest request = new UpdateMyProfileRequest(
                "David",
                "Ilous",
                "taken@example.com"
        );

        User currentUser = new User();
        currentUser.setUsername("david");
        currentUser.setEmail("old@example.com");

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(currentUser, 7L);

        User anotherUser = new User();
        anotherUser.setUsername("someone-else");
        anotherUser.setEmail("taken@example.com");

        idField.set(anotherUser, 8L);

        when(userRepository.findByUsername("david")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(anotherUser));

        assertThatThrownBy(() -> userService.updateMyProfile("david", request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");
    }

    @Test
    void updateMyProfile_success_whenEmailBelongsToSameUser() throws Exception {
        UpdateMyProfileRequest request = new UpdateMyProfileRequest(
                "David",
                "Ilous",
                "same@example.com"
        );

        User currentUser = new User();
        currentUser.setUsername("david");
        currentUser.setFirstName("Old");
        currentUser.setLastName("Name");
        currentUser.setEmail("same@example.com");
        currentUser.setRole(Role.CUSTOMER);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(currentUser, 7L);

        when(userRepository.findByUsername("david")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("same@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        var response = userService.updateMyProfile("david", request);

        assertThat(response.email()).isEqualTo("same@example.com");
        assertThat(response.firstName()).isEqualTo("David");
        assertThat(response.lastName()).isEqualTo("Ilous");
    }
}