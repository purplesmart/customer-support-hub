package com.perfectahr.customer_support_hub.ticket.controller;

import com.perfectahr.customer_support_hub.config.SecurityConfig;
import com.perfectahr.customer_support_hub.exception.GlobalExceptionHandler;
import com.perfectahr.customer_support_hub.ticket.service.TicketService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TicketController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class
)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, TicketControllerValidationTest.TestConfig.class})
class TicketControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

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
    void createTicket_shouldReturn400_whenSubjectIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("cust1")
                                .claim("roles", java.util.List.of("ROLE_CUSTOMER"))))
                        .contentType("application/json")
                        .content("""
                                {
                                  "subject": "",
                                  "description": "Valid description"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.subject").exists());
    }

    @Test
    void createTicket_shouldReturn400_whenDescriptionIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("cust1")
                                .claim("roles", java.util.List.of("ROLE_CUSTOMER"))))
                        .contentType("application/json")
                        .content("""
                                {
                                  "subject": "Valid subject",
                                  "description": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.description").exists());
    }
}