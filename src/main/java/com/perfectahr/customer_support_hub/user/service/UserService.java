package com.perfectahr.customer_support_hub.user.service;

import com.perfectahr.customer_support_hub.user.dto.CreateUserRequest;
import com.perfectahr.customer_support_hub.user.dto.UpdateMyProfileRequest;
import com.perfectahr.customer_support_hub.user.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateMyProfile(String username, UpdateMyProfileRequest request);
}