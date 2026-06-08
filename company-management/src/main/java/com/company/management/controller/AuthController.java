package com.company.management.controller;

import com.company.management.dto.request.LoginRequest;
import com.company.management.dto.response.ApiResponse;
import com.company.management.dto.response.LoginResponse;
import com.company.management.entity.Employee;
import com.company.management.repository.EmployeeRepository;
import com.company.management.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        Employee employee = employeeRepository.findByUsername(request.getUsername()).orElseThrow();

        return ResponseEntity.ok(ApiResponse.ok(LoginResponse.builder()
                .token(token)
                .username(employee.getUsername())
                .role(employee.getRole())
                .build()));
    }
}
