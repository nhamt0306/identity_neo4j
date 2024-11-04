package com.nhamt.book_store.controller;

import com.nhamt.book_store.dto.request.ApiResponse;
import com.nhamt.book_store.dto.request.UserCreationRequest;
import com.nhamt.book_store.dto.request.UserUpdateRequest;
import com.nhamt.book_store.dto.response.UserResponse;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> result = new ApiResponse<>();
        result.setResult(userService.createRequest(request));
        return result;
    }

    @GetMapping()
    List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateRequest(userId,userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return "User has been deleted!";
    }
}
