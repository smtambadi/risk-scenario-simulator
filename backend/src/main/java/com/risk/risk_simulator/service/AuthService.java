package com.risk.risk_simulator.service;

import com.risk.risk_simulator.dto.AuthResponse;
import com.risk.risk_simulator.dto.LoginRequest;
import com.risk.risk_simulator.dto.RegisterRequest;
import com.risk.risk_simulator.entity.User;
import com.risk.risk_simulator.exception.InvalidRiskScenarioException;
import com.risk.risk_simulator.exception.ResourceNotFoundException;
import com.risk.risk_simulator.repository.UserRepository;
import com.risk.risk_simulator.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user with VIEWER role
     */
    public AuthResponse register(RegisterRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Password is required");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new InvalidRiskScenarioException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRiskScenarioException("Email already registered");
        }

        // Create new user with VIEWER role
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("VIEWER")
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Generate tokens
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUsername());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .message("User registered successfully")
                .build();
    }

    /**
     * Login user and return JWT token
     */
    public AuthResponse login(LoginRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Password is required");
        }

        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUsername()));

        // Verify user is active
        if (!user.getIsActive()) {
            throw new InvalidRiskScenarioException("User account is inactive");
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidRiskScenarioException("Invalid credentials");
        }

        // Generate tokens
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    /**
     * Refresh JWT token using refresh token
     */
    public AuthResponse refresh(String refreshToken) {
        // Validate refresh token
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Refresh token is required");
        }

        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new InvalidRiskScenarioException("Invalid or expired refresh token");
        }

        // Extract username and find user
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate new access token
        String newToken = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .message("Token refreshed successfully")
                .build();
    }
}
