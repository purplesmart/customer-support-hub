package com.perfectahr.customer_support_hub.customer.controller;

import com.perfectahr.customer_support_hub.customer.dto.CreateCustomerRequest;
import com.perfectahr.customer_support_hub.customer.dto.CustomerResponse;
import com.perfectahr.customer_support_hub.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents/{agentId}/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@PathVariable Long agentId,
                                           @Valid @RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(agentId, request);
    }

    @GetMapping
    public List<CustomerResponse> getCustomersByAgent(@PathVariable Long agentId) {
        return customerService.getCustomersByAgent(agentId);
    }
}
