package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.request.UserCreationRequest;
import com.nhamt.book_store.dto.request.UserUpdateRequest;
import com.nhamt.book_store.dto.response.UserResponse;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.enums.Role;
import com.nhamt.book_store.exception.AppException;
import com.nhamt.book_store.exception.ErrorCode;
import com.nhamt.book_store.mapper.UserMapper;
import com.nhamt.book_store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // The same with create private final UserRepository userRepo
public class UserService {
    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    public UserResponse createRequest(UserCreationRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Example for builder annotation (Builder pattern) - create new UserCreationRequest object
        UserCreationRequest request1 = UserCreationRequest.builder()
                .username("")
                .firstName("")
                .build();
        // End builder

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //set default role for user
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(){
        List<User> users = userRepository.findAll();
        log.warn("This is in method Get all users.");
        /*List<UserResponse> responseList = new ArrayList<>();
        userRepository.findAll().forEach(s -> responseList.add(userMapper.toUserResponse(s)));*/
        return userRepository.findAll().stream().map(s -> userMapper.toUserResponse(s)).toList();
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.username == authentication.name")
    public UserResponse getUserById(String id) {
        log.warn("This is in method Get user by Id.");
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    public UserResponse getMyInfo(){
        Authentication authenticationInfo = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationInfo.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateRequest(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
