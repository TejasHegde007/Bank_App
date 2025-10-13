package com.bankingapp.userservice.service.impl;

import com.bankingapp.userservice.dto.RegisterUserRequest;
import com.bankingapp.userservice.entity.User;
import com.bankingapp.userservice.enums.UserRole;
import com.bankingapp.userservice.enums.UserStatus;
import com.bankingapp.userservice.repository.UserRepository;
import com.bankingapp.userservice.service.KeycloakService;
import com.bankingapp.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    /**
     * Generic method to create a user in the database.
     */
    @Override
    public CompletableFuture<User> createUser(User user) {
        return CompletableFuture.supplyAsync(() -> userRepository.save(user));
    }

    /**
     * Public customer self-registration flow.
     * 1. Validates duplicate email.
     * 2. Creates user in Keycloak with CUSTOMER role.
     * 3. Persists user in Oracle DB.
     */
    @Override
    public CompletableFuture<User> registerCustomer(RegisterUserRequest dto) {
        return CompletableFuture.supplyAsync(() -> {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already registered.");
            }

            // 1️⃣ Create Keycloak account (CUSTOMER role)
            keycloakService.createKeycloakUser(
                    dto.getEmail(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getPassword(),
                    "CUSTOMER"
            );

            // 2️⃣ Save in DB
            User user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setRole(UserRole.CUSTOMER);
            user.setStatus(UserStatus.ACTIVE);

            return userRepository.save(user);
        });
    }

    /**
     * Admin creates new user (ADMIN or MANAGER).
     */
    @Override
    public CompletableFuture<User> registerAdmin(RegisterUserRequest dto, UserRole role) {
        return CompletableFuture.supplyAsync(() -> {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already registered.");
            }

            // 1️⃣ Create user in Keycloak with ADMIN or MANAGER role
            keycloakService.createKeycloakUser(
                    dto.getEmail(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getPassword(),
                    role.name()
            );

            // 2️⃣ Persist in Oracle
            User user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setRole(role);
            user.setStatus(UserStatus.ACTIVE);

            return userRepository.save(user);
        });
    }

    /**
     * Retrieve all users.
     */
    @Override
    public CompletableFuture<List<User>> getAllUsers() {
        return CompletableFuture.supplyAsync(userRepository::findAll);
    }

    /**
     * Retrieve a user by ID.
     */
    @Override
    public CompletableFuture<User> getUserById(Long  id) {
        return CompletableFuture.supplyAsync(() ->
                userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    /**
     * Update an existing user.
     */
    @Override
    public CompletableFuture<User> updateUser(Long  id, User updatedUser) {
        return CompletableFuture.supplyAsync(() -> {
            User existing = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existing.setFirstName(updatedUser.getFirstName());
            existing.setLastName(updatedUser.getLastName());
            existing.setEmail(updatedUser.getEmail());
            existing.setPhoneNumber(updatedUser.getPhoneNumber());
            existing.setRole(updatedUser.getRole());
            existing.setStatus(updatedUser.getStatus());

            return userRepository.save(existing);
        });
    }

    /**
     * Delete a user by ID.
     */
    @Override
    public CompletableFuture<Void> deleteUser(Long  id) {
        return CompletableFuture.runAsync(() -> {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User not found");
            }
            userRepository.deleteById(id);
        });
    }
}
