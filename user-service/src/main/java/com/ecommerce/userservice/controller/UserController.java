package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.ApiResponse;
import com.ecommerce.userservice.dto.UpdateProfileRequest;
import com.ecommerce.userservice.dto.UserProfileDto;
import com.ecommerce.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserProfileDto> getProfile(@RequestHeader("X-User-Id") String userId) {
        return ApiResponse.success(userService.getProfile(Long.valueOf(userId)));
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileDto> updateProfile(@RequestHeader("X-User-Id") String userId,
                                                     @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success("Profile updated successfully", userService.updateProfile(Long.valueOf(userId), request));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserProfileDto> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getProfile(id));
    }
}
