package com.internetbanking.user.service;

import com.internetbanking.common.email.EmailTemplates;
import com.internetbanking.notification.service.MailService;
import com.internetbanking.user.dto.Role;
import com.internetbanking.user.dto.UserRequestDto;
import com.internetbanking.user.dto.UserResponseDTO;
import com.internetbanking.user.dto.UserStatus;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.exception.EmailAlreadyExistsException;
import com.internetbanking.user.exception.UserNameAlreadyExistsException;
import com.internetbanking.user.exception.UserNotFoundException;
import com.internetbanking.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserWritePlatformServiceImpl implements UserWritePlatformService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public UserWritePlatformServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("A User with this email " + "already exists " + userRequestDto.getEmail());
        }

        if (userRepository.existsByUserName(userRequestDto.getUserName())) {
            throw new UserNameAlreadyExistsException("A User with this user name " + "already exists " + userRequestDto.getUserName());
        }

        User newUser = new User();
        newUser.setEmail(userRequestDto.getEmail());
        newUser.setUserName(userRequestDto.getUserName());
        newUser.setFirstName(userRequestDto.getFirstName());
        newUser.setLastName(userRequestDto.getLastName());
        newUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        newUser.setAddress(userRequestDto.getAddress());
        newUser.setPhoneNumber(userRequestDto.getPhoneNumber());
        newUser.setRole(Role.USER);
        newUser.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(newUser);

        String[] email = EmailTemplates.userRegistration(savedUser.getFirstName());

        try {
            mailService.sendSimpleEmail(savedUser.getEmail(), email[0], email[1]);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send account creation email: ", e );
        }

        return mapToResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(int id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!existingUser.getEmail().equals(userRequestDto.getEmail()) && userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("A User with this email already exists: " + userRequestDto.getEmail());
        }

        if (!existingUser.getUserName().equals(userRequestDto.getUserName()) && userRepository.existsByUserName(userRequestDto.getUserName())) {
            throw new UserNameAlreadyExistsException("A User with this email already exists: " + userRequestDto.getEmail());
        }

        existingUser.setEmail(userRequestDto.getEmail());
        existingUser.setUserName(userRequestDto.getUserName());
        existingUser.setFirstName(userRequestDto.getFirstName());
        existingUser.setLastName(userRequestDto.getLastName());
        existingUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        existingUser.setAddress(userRequestDto.getAddress());
        existingUser.setPhoneNumber(userRequestDto.getPhoneNumber());
        existingUser.setRole(Role.USER);
        existingUser.setStatus(UserStatus.ACTIVE);

        User updatedUser = userRepository.save(existingUser);

        return mapToResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        existingUser.setStatus(UserStatus.INACTIVE);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
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
        dto.setUserStatus(user.getStatus());
        return dto;
    }
}
