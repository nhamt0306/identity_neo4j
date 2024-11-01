package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.UserCreationRequest;
import com.nhamt.book_store.dto.UserUpdateRequest;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.exception.AppException;
import com.nhamt.book_store.exception.ErrorCode;
import com.nhamt.book_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createRequest(UserCreationRequest request){
        User user = new User();
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setDob(request.getDob());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public User updateRequest(String userId, UserUpdateRequest request){
        User user = getUserById(userId);
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
