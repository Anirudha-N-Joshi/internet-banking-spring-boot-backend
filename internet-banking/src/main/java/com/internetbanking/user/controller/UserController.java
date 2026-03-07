package com.internetbanking.user.controller;

import com.internetbanking.user.dto.UserRequestDto;
import com.internetbanking.user.dto.UserResponseDTO;
import com.internetbanking.user.service.UserReadPlatformService;
import com.internetbanking.user.service.UserWritePlatformService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserReadPlatformService userReadPlatformService;
    private final UserWritePlatformService userWritePlatformService;

    public UserController(final UserReadPlatformService userReadPlatformService, final UserWritePlatformService userWritePlatformService) {
        this.userReadPlatformService = userReadPlatformService;
        this.userWritePlatformService = userWritePlatformService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {

        List<UserResponseDTO> users = userReadPlatformService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@RequestParam Integer userId) {

        UserResponseDTO currentUser = userReadPlatformService.getCurrentUser(userId);

        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser( @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDTO userResponseDTO = userWritePlatformService.createUser(userRequestDto);

        return ResponseEntity.ok().body(userResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable int id, @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDTO updatedUser = userWritePlatformService.updateUser(id, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userWritePlatformService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
