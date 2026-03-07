package com.internetbanking.user.service;

import com.internetbanking.user.dto.UserResponseDTO;
import com.internetbanking.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserReadPlatformServiceImpl implements UserReadPlatformService {

    private final UserRepository userRepository;

    public UserReadPlatformServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    dto.setUserName(user.getUserName());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setCreatedDate(user.getCreatedDate());
                    dto.setRole(user.getRole().name());
                    dto.setAddress(user.getAddress());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    return dto;
                })
                .toList();
    }

    @Override
    public UserResponseDTO getCurrentUser(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    dto.setUserName(user.getUserName());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setCreatedDate(user.getCreatedDate());
                    dto.setRole(user.getRole().name());
                    dto.setAddress(user.getAddress());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

}
