package com.perfectahr.customer_support_hub.auth.service;

import com.perfectahr.customer_support_hub.auth.dto.LoginRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}