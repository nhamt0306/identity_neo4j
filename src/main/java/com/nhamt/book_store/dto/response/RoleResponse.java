package com.nhamt.book_store.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder //-> Builder pattern in design pattern. See example in create user method (User service)
@FieldDefaults(level = AccessLevel.PRIVATE) // set default data type for all variable is private
public class RoleResponse {
    private String name;
    private String description;
    Set<PermissionResponse> permissions;

}
