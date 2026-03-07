package com.internetbanking.authentication.service;

import com.internetbanking.authentication.dto.LoginRequestDTO;
import com.internetbanking.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationWritePlatformServiceImpl implements AuthenticationWritePlatformService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationWritePlatformServiceImpl(final UserRepository userRepository, final JwtUtil jwtUtil, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        return userRepository.findByUserName(loginRequestDTO.getUserName())
                .filter(user -> passwordEncoder.matches(
                        loginRequestDTO.getPassword(), user.getPassword()))
                .map(user -> jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId(), user.getUserName()));
    }
}
