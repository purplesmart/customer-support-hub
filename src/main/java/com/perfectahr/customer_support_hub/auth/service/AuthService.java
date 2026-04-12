package com.perfectahr.customer_support_hub.auth.service;

import com.perfectahr.customer_support_hub.auth.dto.CreateAdminRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginResponse;
import com.perfectahr.customer_support_hub.user.dto.UserResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UserResponse createAdmin(CreateAdminRequest request);
}