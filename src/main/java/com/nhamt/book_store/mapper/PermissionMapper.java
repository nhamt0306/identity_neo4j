package com.nhamt.book_store.mapper;

import com.nhamt.book_store.dto.request.PermissionRequest;
import com.nhamt.book_store.dto.response.PermissionResponse;
import com.nhamt.book_store.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
