package com.internetbanking.user.service;

import com.internetbanking.user.dto.UserRequestDto;
import com.internetbanking.user.dto.UserResponseDTO;

public interface UserWritePlatformService {

    UserResponseDTO createUser(UserRequestDto userRequestDto);

    UserResponseDTO updateUser(int id, UserRequestDto userRequestDto);

    void deleteUser(int id);
}
