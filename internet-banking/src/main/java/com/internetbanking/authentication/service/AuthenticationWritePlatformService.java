package com.internetbanking.authentication.service;

import com.internetbanking.authentication.dto.LoginRequestDTO;

import java.util.Optional;

public interface AuthenticationWritePlatformService {
    Optional<String> authenticate(LoginRequestDTO loginRequestDTO);
}
