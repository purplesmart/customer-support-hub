package com.perfectahr.customer_support_hub.user.controller;

import com.perfectahr.customer_support_hub.user.dto.CreateUserRequest;
import com.perfectahr.customer_support_hub.user.dto.UpdateMyProfileRequest;
import com.perfectahr.customer_support_hub.user.dto.UserResponse;
import com.perfectahr.customer_support_hub.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(Authentication authentication,
                                        @Valid @RequestBody UpdateMyProfileRequest request) {
        return userService.updateMyProfile(authentication.getName(), request);
    }
}