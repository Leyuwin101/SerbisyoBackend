package com.example.serbisyofullstack.security;

import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.UnauthorizedException;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.enums.RoleEnum;
import com.example.serbisyofullstack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Single point of access for the authenticated principal. Controllers and
 * services call this instead of touching SecurityContextHolder directly.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    /**
     * @return the authenticated user's id.
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        try {
            return Long.valueOf(auth.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Invalid authentication principal");
        }
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new UnauthorizedException("User no longer exists"));
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() != null;
    }

    public RoleEnum getCurrentRole() {
        if (hasRole("ADMIN")) {
            return RoleEnum.ADMIN;
        }
        if (hasRole("PROVIDER")) {
            return RoleEnum.PROVIDER;
        }
        if (hasRole("MODERATOR")) {
            return RoleEnum.MODERATOR;
        }
        if (hasRole("SUPPORT")) {
            return RoleEnum.SUPPORT;
        }
        return RoleEnum.CUSTOMER;
    }

    /**
     * Ownership guard used by services.
     */
    public void assertOwnership(Long ownerId, String message) {
        if (!getCurrentUserId().equals(ownerId)) {
            throw new ForbiddenException(message);
        }
    }
}
