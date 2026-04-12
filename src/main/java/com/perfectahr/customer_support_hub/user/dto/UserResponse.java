package com.perfectahr.customer_support_hub.user.dto;

import com.perfectahr.customer_support_hub.entity.Role;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        Role role
) {}