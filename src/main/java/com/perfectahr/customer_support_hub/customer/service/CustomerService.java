package com.perfectahr.customer_support_hub.customer.service;

import com.perfectahr.customer_support_hub.customer.dto.CreateCustomerRequest;
import com.perfectahr.customer_support_hub.customer.dto.CustomerResponse;
import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(Long agentId, CreateCustomerRequest request);

    List<CustomerResponse> getCustomersByAgent(Long agentId);
}
