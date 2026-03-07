package com.internetbanking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestDto {

    @NotBlank(message = "First Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "User Name is required")
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password size should be in the range of 8 and 20")
    private String password;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;

//    @NotNull(message = "Role is required")
//    private Role role;

    // Getter and Setters
    public @NotBlank(message = "First Name is required") @Size(max = 100, message = "First Name cannot exceed 100 characters") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(message = "First Name is required") @Size(max = 100, message = "First Name cannot exceed 100 characters") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "Last Name is required") @Size(max = 100, message = "Last Name cannot exceed 100 characters") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(message = "Last Name is required") @Size(max = 100, message = "Last Name cannot exceed 100 characters") String lastName) {
        this.lastName = lastName;
    }

    public @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password size should be in the range of 8 and 20") String getPassword() {
        return password;
    }

//    public @NotNull(message = "Role is required") Role getRole() {
//        return role;
//    }
//
//    public void setRole(@NotNull(message = "Role is required") Role role) {
//        this.role = role;
//    }

    public @NotBlank(message = "User Name is required") @Size(max = 100, message = "User Name cannot exceed 100 characters") String getUserName() {
        return userName;
    }

    public void setUserName(@NotBlank(message = "User Name is required") @Size(max = 20, message = "User Name cannot exceed 20 characters") String userName) {
        this.userName = userName;
    }

    public @NotBlank(message = "Address is required") @Size(max = 200, message = "Address cannot exceed 200 characters") String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(message = "Address is required") @Size(max = 200, message = "Address cannot exceed 200 characters") String address) {
        this.address = address;
    }

    public @NotBlank(message = "Phone Number is required") @Size(max = 15, message = "Phone Number cannot exceed 15 characters") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotBlank(message = "Phone Number is required") @Size(max = 15, message = "Phone Number cannot exceed 15 characters") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
