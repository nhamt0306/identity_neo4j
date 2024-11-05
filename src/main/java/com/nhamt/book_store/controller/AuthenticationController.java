package com.nhamt.book_store.controller;

import com.nhamt.book_store.dto.request.ApiResponse;
import com.nhamt.book_store.dto.request.AuthenticationRequest;
import com.nhamt.book_store.dto.response.AuthenticationResponse;
import com.nhamt.book_store.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        AuthenticationResponse result = authenticationService.authenticate(request);
        ApiResponse response = ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .result(result)
                .build();
        ApiResponse response2 = new ApiResponse();
        response2.setResult(result);
        return response;
    }
}
