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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/create")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<UserResponse> result = new ApiResponse<>();
        result.setResult(userService.createRequest(request));
        return result;
    }

    @GetMapping()
    List<User> getUsers(){
        return userService.getUsers();
    }

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
