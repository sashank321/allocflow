package com.ramas.service;

import com.ramas.dto.AuthDtos.*;
import com.ramas.entity.User;
import com.ramas.enums.AuditAction;
import com.ramas.enums.UserRole;
import com.ramas.exception.BadRequestException;
import com.ramas.exception.ResourceNotFoundException;
import com.ramas.repository.UserRepository;
import com.ramas.security.JwtProperties;
import com.ramas.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                       JwtProperties jwtProperties, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
        this.auditService = auditService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, String ipAddress) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BadRequestException("An account with email '" + request.email() + "' already exists");
        }

        UserRole role = request.role() != null ? request.role() : UserRole.AUTHOR;
        User user = new User(
                request.email().toLowerCase().trim(),
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                request.affiliation(),
                role
        );

        User savedUser = userRepository.save(user);
        auditService.log(savedUser.getEmail(), AuditAction.LOGIN, "USER", savedUser.getId().toString(), "User registered", ipAddress);

        String token = tokenProvider.generateTokenForEmailAndRole(savedUser.getEmail(), savedUser.getRole().name());
        return AuthResponse.of(token, jwtProperties.getExpirationMs(), toDto(savedUser));
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase().trim(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmailIgnoreCase(request.email().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        auditService.log(user.getEmail(), AuditAction.LOGIN, "USER", user.getId().toString(), "Successful user login", ipAddress);

        return AuthResponse.of(token, jwtProperties.getExpirationMs(), toDto(user));
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return toDto(user);
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAffiliation(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
