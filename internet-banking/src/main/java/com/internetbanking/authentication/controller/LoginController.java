package com.internetbanking.authentication.controller;

import com.internetbanking.authentication.dto.LoginRequestDTO;
import com.internetbanking.authentication.dto.LoginResponseDTO;
import com.internetbanking.authentication.dto.RefreshRequestDTO;
import com.internetbanking.authentication.dto.RefreshResponseDTO;
import com.internetbanking.authentication.entity.RefreshToken;
import com.internetbanking.authentication.service.AuthenticationWritePlatformService;
import com.internetbanking.authentication.service.JwtUtil;
import com.internetbanking.authentication.service.RefreshTokenService;
import com.internetbanking.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/authentication")
public class LoginController {
    private final AuthenticationWritePlatformService authenticationWritePlatformService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public LoginController(final AuthenticationWritePlatformService authenticationWritePlatformService,
                           final RefreshTokenService refreshTokenService, final JwtUtil jwtUtil) {
        this.authenticationWritePlatformService = authenticationWritePlatformService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<String> tokenOptional = authenticationWritePlatformService.authenticate(loginRequestDTO);

        if(tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String accessToken = tokenOptional.get();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequestDTO.getUserName());

        return ResponseEntity.ok().body(new LoginResponseDTO(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        try {
            RefreshToken refreshToken = refreshTokenService
                    .verifyRefreshToken(request.getRefreshToken());

            User user = refreshToken.getUser();

            String newAccessToken = jwtUtil.generateToken(
                    user.getEmail(), user.getRole(),
                    user.getId(), user.getUserName()
            );

            return ResponseEntity.ok(new RefreshResponseDTO(newAccessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequestDTO request) {
        try {
            RefreshToken refreshToken = refreshTokenService
                    .verifyRefreshToken(request.getRefreshToken());
            refreshTokenService.deleteByUser(refreshToken.getUser());
        } catch (RuntimeException ignored) {}
        return ResponseEntity.noContent().build();
    }

}
