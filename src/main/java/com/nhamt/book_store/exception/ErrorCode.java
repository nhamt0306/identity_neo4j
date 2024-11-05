package com.nhamt.book_store.exception;

public enum ErrorCode {
    INVALID_MESSAGE_KEY(9999, "Uncategorized error."),
    INVALID_SIGN_JWT(1999, "Cannot sign this JWT token."),
    USER_EXISTED(1001, "User existed."),
    USER_NOT_FOUND(1002, "User not found."),
    UNAUTHENTICATED(1005, "Unauthenticated."),
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized error."),
    USERNAME_INVALID(1003, "Username must be at least 3 characters."),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters.")
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
