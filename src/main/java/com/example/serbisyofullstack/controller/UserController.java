package com.example.serbisyofullstack.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.dto.request.user.UpdateUserRequest;
import com.example.serbisyofullstack.dto.response.user.UpdateUserResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Self-service user profile endpoints. The user id always comes from the
 * authenticated principal; admins use {@code AdminController} for other users.
 */
@Tag(name = "Users", description = "Self-service profile of the authenticated user")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Get the authenticated user's profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<UserSummaryDto> getMe() {
        return ResponseEntity.ok(userService.getUser(currentUserService.getCurrentUserId()));
    }

    @Operation(summary = "Update the authenticated user's email or phone")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PatchMapping("/me")
    public ResponseEntity<UpdateUserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "Deactivate the authenticated user's account")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Account deactivated"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateMe() {
        userService.deactivateAccount(currentUserService.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
