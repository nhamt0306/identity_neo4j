package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.request.UserCreationRequest;
import com.nhamt.book_store.dto.request.UserUpdateRequest;
import com.nhamt.book_store.dto.response.UserResponse;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.exception.AppException;
import com.nhamt.book_store.exception.ErrorCode;
import com.nhamt.book_store.mapper.UserMapper;
import com.nhamt.book_store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // The same with create private final UserRepository userRepo
public class UserService {
    UserRepository userRepository;

    UserMapper userMapper;

    public User createRequest(UserCreationRequest request){
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

        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
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
