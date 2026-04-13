package com.perfectahr.customer_support_hub.user.service;

import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.DuplicateResourceException;
import com.perfectahr.customer_support_hub.exception.NotFoundException;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import com.perfectahr.customer_support_hub.user.dto.CreateUserRequest;
import com.perfectahr.customer_support_hub.user.dto.UpdateMyProfileRequest;
import com.perfectahr.customer_support_hub.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setRole(request.role());

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getRole()
        );
    }

    @Override
    public UserResponse updateMyProfile(String username, UpdateMyProfileRequest request) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        boolean emailBelongsToAnotherUser = userRepository.findByEmail(request.email())
                .filter(existingUser -> !existingUser.getId().equals(currentUser.getId()))
                .isPresent();

        if (emailBelongsToAnotherUser) {
            throw new DuplicateResourceException("Email already exists");
        }

        currentUser.setFirstName(request.firstName());
        currentUser.setLastName(request.lastName());
        currentUser.setEmail(request.email());

        User saved = userRepository.save(currentUser);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getRole()
        );
    }
}