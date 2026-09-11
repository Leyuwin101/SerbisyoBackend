package com.example.serbisyofullstack.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Loads the user from the database for JWT-authenticated requests and bridges
 * the domain roles into Spring Security authorities.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Long id;
        try {
            id = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user identifier");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUserId()),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + primaryRole(user))));
    }

    private String primaryRole(User user) {
        return user.getRoles() == null || user.getRoles().isEmpty()
                ? "CUSTOMER"
                : user.getRoles().iterator().next().getRole().getName().name();
    }

    public List<GrantedAuthority> authoritiesFor(User user) {
        return user.getRoles().stream()
                .map(ur -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName().name()))
                .toList();
    }
}
