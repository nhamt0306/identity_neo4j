package com.nhamt.book_store.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@Getter
public enum ErrorCode {
    INVALID_MESSAGE_KEY(9999, "Uncategorized error.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(2999, "Access denied.", HttpStatus.BAD_REQUEST),
    INVALID_SIGN_JWT(1999, "Cannot sign this JWT token.", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1005, "You do not have permission.", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1006, "Unauthenticated.", HttpStatus.UNAUTHORIZED),
    PARSE_TOKEN_FAILED(1007, "Parse token to introspect failed.",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized error.", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1003, "Username must be at least 3 characters.",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters.",HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;

}
