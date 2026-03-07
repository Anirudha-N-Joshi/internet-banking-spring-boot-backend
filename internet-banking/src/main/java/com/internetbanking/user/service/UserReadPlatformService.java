package com.internetbanking.user.service;

import com.internetbanking.user.dto.UserResponseDTO;

import java.util.List;

public interface UserReadPlatformService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getCurrentUser(Integer userId);

}
