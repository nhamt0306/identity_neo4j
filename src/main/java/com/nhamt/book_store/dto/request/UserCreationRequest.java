package com.nhamt.book_store.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder //-> Builder pattern in design pattern. See example in create user method (User service)
@FieldDefaults(level = AccessLevel.PRIVATE) // set default data type for all variable is private
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;

    LocalDate dob;

}
