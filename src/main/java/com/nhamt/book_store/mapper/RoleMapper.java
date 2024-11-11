package com.nhamt.book_store.mapper;

import com.nhamt.book_store.dto.request.PermissionRequest;
import com.nhamt.book_store.dto.request.RoleRequest;
import com.nhamt.book_store.dto.response.PermissionResponse;
import com.nhamt.book_store.dto.response.RoleResponse;
import com.nhamt.book_store.entity.Permission;
import com.nhamt.book_store.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
