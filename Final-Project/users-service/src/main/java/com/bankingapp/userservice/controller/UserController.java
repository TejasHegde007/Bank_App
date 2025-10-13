package com.bankingapp.userservice.controller;

import com.bankingapp.userservice.dto.RegisterUserRequest;
import com.bankingapp.userservice.entity.User;
import com.bankingapp.userservice.enums.UserRole;
import com.bankingapp.userservice.exception.ErrorResponse;
import com.bankingapp.userservice.exception.UserServiceException;
import com.bankingapp.userservice.service.UserService;
import com.bankingapp.userservice.util.ErrorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ErrorUtil errorUtil;

    // ✅ Public health endpoint (no auth)
    @GetMapping("/public/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Users Service is up and running 🚀");
    }

    // ✅ Public registration (no authentication)
    @PostMapping("/public/register")
    public ResponseEntity<?> registerCustomer(@RequestBody RegisterUserRequest request) {
        try {
            return ResponseEntity.ok(userService.registerCustomer(request).get());
        } catch (ExecutionException | InterruptedException e) {
            return handleException(e);
        } catch (Exception e) {
            return handleUnexpected(e);
        }
    }

    // ✅ Admin registration (for ADMIN role only)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterUserRequest request) {
        try {
            return ResponseEntity.ok(userService.registerAdmin(request, UserRole.ADMIN).get());
        } catch (ExecutionException | InterruptedException e) {
            return handleException(e);
        } catch (Exception e) {
            return handleUnexpected(e);
        }
    }

    // ✅ Get all users (ADMIN, MANAGER)
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/manager/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers().get());
        } catch (ExecutionException | InterruptedException e) {
            return handleException(e);
        } catch (Exception e) {
            return handleUnexpected(e);
        }
    }

    // ✅ Get user by ID (CUSTOMER, MANAGER, ADMIN)
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'MANAGER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable  Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id).get());
        } catch (ExecutionException | InterruptedException e) {
            return handleException(e);
        } catch (Exception e) {
            return handleUnexpected(e);
        }
    }

    // ✅ Update user (ADMIN only)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long  id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user).get());
        } catch (ExecutionException | InterruptedException e) {
            return handleException(e);
        } catch (Exception e) {
            return handleUnexpected(e);
        }
    }

    // ✅ Delete user (ADMIN only)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long  id) {
        try {
            userService.deleteUser(id).get();
            return ResponseEntity.ok("User deleted successfully");
        } catch (ExecutionException | InterruptedException e) {
            return handleException(e);
        } catch (Exception e) {
            return handleUnexpected(e);
        }
    }

    // ✅ Debug endpoint to print roles from token
    @GetMapping("/debug/roles")
    public ResponseEntity<?> debugRoles(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities());
    }

    // ⚙️ Handle service exceptions
    private ResponseEntity<ErrorResponse> handleException(Exception e) {
        if (e.getCause() instanceof UserServiceException ex) {
            ErrorResponse error = errorUtil.buildError(ex);
            return ResponseEntity.status(400).body(error);
        }

        ErrorResponse error = ErrorResponse.builder()
                .errorCode("ERR_99")
                .message("Unexpected service error")
                .traceId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.internalServerError().body(error);
    }

    // ⚙️ Handle unexpected errors (non-service)
    private ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("ERR_98")
                .message(e.getMessage())
                .traceId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.internalServerError().body(error);
    }
}
