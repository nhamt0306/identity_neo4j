package com.nhamt.book_store.controller;

import com.nhamt.book_store.dto.request.ApiResponse;
import com.nhamt.book_store.dto.request.AuthenticationRequest;
import com.nhamt.book_store.dto.request.IntrospectRequest;
import com.nhamt.book_store.dto.response.AuthenticationResponse;
import com.nhamt.book_store.dto.response.IntrospectResponse;
import com.nhamt.book_store.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        AuthenticationResponse result = authenticationService.authenticate(request);
        ApiResponse response = ApiResponse.<AuthenticationResponse>builder()
                .code(1000) // successfully code
                .result(result)
                .build();
        /* The same with code below:
        ApiResponse response2 = new ApiResponse();
        response2.setResult(result);
        */
        return response;
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        ApiResponse response = ApiResponse.<IntrospectResponse>builder()
                .code(1000) // successfully code
                .result(result)
                .build();
        return response;
    }
}
