package com.perfectahr.customer_support_hub.user.controller;

import com.perfectahr.customer_support_hub.config.SecurityConfig;
import com.perfectahr.customer_support_hub.exception.GlobalExceptionHandler;
import com.perfectahr.customer_support_hub.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class
)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, UserControllerValidationTest.TestConfig.class})
class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        JwtDecoder jwtDecoder() {
            return token -> {
                throw new UnsupportedOperationException("Not used in this test");
            };
        }
    }

    @Test
    void updateMyProfile_shouldReturn400_whenFirstNameIsBlank() throws Exception {
        mockMvc.perform(put("/api/v1/users/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("david")
                                .claim("roles", java.util.List.of("ROLE_CUSTOMER"))))
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Ilous",
                                  "email": "david@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void updateMyProfile_shouldReturn400_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/users/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("david")
                                .claim("roles", java.util.List.of("ROLE_CUSTOMER"))))
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "David",
                                  "lastName": "Ilous",
                                  "email": "not-an-email"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void updateMyProfile_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(put("/api/v1/users/me")
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "David",
                                  "lastName": "Ilous",
                                  "email": "david@example.com"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}