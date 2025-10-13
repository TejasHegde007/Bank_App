package com.bankingapp.userservice.service;

import com.bankingapp.userservice.dto.RegisterUserRequest;
import com.bankingapp.userservice.entity.User;
import com.bankingapp.userservice.enums.UserRole;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    CompletableFuture<User> createUser(User user);

    CompletableFuture<User> registerCustomer(RegisterUserRequest dto);

    CompletableFuture<User> registerAdmin(RegisterUserRequest dto, UserRole role);

    CompletableFuture<List<User>> getAllUsers();

    CompletableFuture<User> getUserById(Long  id);

    CompletableFuture<User> updateUser(Long  id, User updatedUser);

    CompletableFuture<Void> deleteUser(Long  id);
}
