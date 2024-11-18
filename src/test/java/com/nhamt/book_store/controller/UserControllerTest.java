package com.nhamt.book_store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhamt.book_store.dto.request.UserCreationRequest;
import com.nhamt.book_store.dto.response.UserResponse;
import com.nhamt.book_store.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class UserControllerTest {
    //Inject needed
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    //Define input and output
    private UserCreationRequest request;
    private UserResponse userResponse;
    private LocalDate dob;

    //Init data before running test
    @BeforeEach
    private void initData(){
        dob = LocalDate.of(2001,6,3);

        request = UserCreationRequest.builder()
                .username("Nha")
                .lastName("Mai")
                .username("nhamai0306")
                .password("thanhnha")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("cf0600f538b3")
                .username("Nha")
                .lastName("Mai")
                .username("nhamai0306")
                .dob(dob)
                .build();
    };
    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN: Input data (Knowledge)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String content = objectMapper.writeValueAsString(request);

        Mockito.when(userService.createRequest(ArgumentMatchers.any()))
                        .thenReturn(userResponse);

        //WHEN: What do you want to test?
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000)
        );

    }
}
