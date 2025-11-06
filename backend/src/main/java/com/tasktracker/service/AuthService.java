package com.tasktracker.service;

import com.tasktracker.dto.LoginRequest;
import com.tasktracker.dto.LoginResponse;
import com.tasktracker.model.User;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String token = jwtUtil.generateToken(user.getUsername());
            
            return new LoginResponse(token, user.getUsername(), user.getEmail(), "Login successful");
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
    
    public User createDummyUser() {
        if (userRepository.existsByUsername("demo")) {
            return userRepository.findByUsername("demo").get();
        }
        
        User user = new User();
        user.setUsername("demo");
        user.setPassword(passwordEncoder.encode("demo123"));
        user.setEmail("demo@tasktracker.com");
        user.setRole("USER");
        
        return userRepository.save(user);
    }
}

