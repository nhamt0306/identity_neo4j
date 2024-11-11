package com.nhamt.book_store.controller;

import com.nhamt.book_store.dto.request.ApiResponse;
import com.nhamt.book_store.dto.request.UserCreationRequest;
import com.nhamt.book_store.dto.request.UserUpdateRequest;
import com.nhamt.book_store.dto.response.UserResponse;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("/create")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<UserResponse> result = new ApiResponse<>();
        result.setResult(userService.createRequest(request));
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')") // this API need permission of admin to access
    @GetMapping()
    ApiResponse<List<UserResponse>> getUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.warn("username: "+ authentication.getName());
        authentication.getAuthorities().forEach(s -> log.warn("role: " + s));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') || returnObject.username == authentication.name")
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.username == authentication.name")
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId) {
        ApiResponse<UserResponse> result = new ApiResponse<>();
        result.setResult(userService.getUserById(userId));
        return result;
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        ApiResponse<UserResponse> result = new ApiResponse<>();
        result.setResult(userService.updateRequest(userId,userUpdateRequest));
        return result;
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return "User has been deleted!";
    }
}
