package com.risk.risk_simulator.controller;

import com.risk.risk_simulator.dto.AuthResponse;
import com.risk.risk_simulator.dto.LoginRequest;
import com.risk.risk_simulator.dto.RegisterRequest;
import com.risk.risk_simulator.dto.RefreshRequest;
import com.risk.risk_simulator.service.AuthService;
import com.risk.risk_simulator.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * POST /api/auth/register - Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message(e.getMessage()).build());
        }
    }

    /**
     * POST /api/auth/login - Login user and return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message(e.getMessage()).build());
        }
    }

    /**
     * POST /api/auth/refresh - Refresh JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        try {
            AuthResponse response = authService.refresh(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message(e.getMessage()).build());
        }
    }

    /**
     * GET /api/auth/verify - Verify JWT token and return user info
     * Used by frontend to check if stored token is still valid
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Missing or invalid Authorization header"));
            }
            String token = authHeader.substring(7);
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Token is invalid or expired"));
            }
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("username", username);
            userData.put("role", role);
            userData.put("name", username.substring(0, 1).toUpperCase() + username.substring(1) + " User");
            userData.put("email", username + "@risksim.com");
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token verification failed"));
        }
    }
}
