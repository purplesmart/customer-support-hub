package com.perfectahr.customer_support_hub.auth.service;

import com.perfectahr.customer_support_hub.auth.dto.LoginRequest;
import com.perfectahr.customer_support_hub.auth.jwt.JwtHeader;
import com.perfectahr.customer_support_hub.auth.jwt.JwtTokenService;
import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("test");
        user.setPassword("encoded-password");
        user.setRole(Role.ADMIN);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("test", "raw-password");

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        JwtHeader jwtHeader = new JwtHeader("token", Instant.now().plusSeconds(3600));

        when(jwtTokenService.generateToken(eq(user), any()))
                .thenReturn(jwtHeader);

        var response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("token");
        assertThat(response.username()).isEqualTo("test");
        assertThat(response.roles()).contains("ROLE_ADMIN");
    }

    @Test
    void login_shouldFail_whenWrongPassword() {
        LoginRequest request = new LoginRequest("test", "wrong");

        when(passwordEncoder.matches("wrong", "encoded-password"))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_shouldFail_whenUserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("unknown", "pass");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}