package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.request.UserCreationRequest;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.exception.AppException;
import com.nhamt.book_store.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    private UserRepository userRepository;

    //Define input and output
    private UserCreationRequest request;
    private User user;
    private LocalDate dob;

    //Init data before running test
    @BeforeEach
    private void initData() {
        dob = LocalDate.of(2001, 6, 3);

        request = UserCreationRequest.builder()
                .firstName("Nha")
                .lastName("Mai")
                .username("nhamai0306")
                .password("thanhnha")
                .dob(dob)
                .build();

        user = User.builder()
                .id("cf0600f538b3")
                .firstName("Nha")
                .lastName("Mai")
                .username("nhamai0306")
                .dob(dob)
                .build();
    };

    @Test
    void createUser_validRequest_success(){
        //GIVEN
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(userRepository.save(any())).thenReturn(user);

        //WHEN
        var result = userService.createRequest(request);

        //THEN
        Assertions.assertThat(result.getUsername()).isEqualTo("nhamai0306");
        Assertions.assertThat(result.getFirstName()).isEqualTo("Nha");
        Assertions.assertThat(result.getLastName()).isEqualTo("Mai");
        //....
    }

    @Test
    void createUser_userExited_fail(){
        //GIVEN
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //WHEN
        var exception = org.junit.jupiter.api.Assertions.assertThrows(AppException.class, () -> userService.createRequest(request));

        //THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
    }

}
