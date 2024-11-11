package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.request.PermissionRequest;
import com.nhamt.book_store.dto.response.PermissionResponse;
import com.nhamt.book_store.entity.Permission;
import com.nhamt.book_store.mapper.PermissionMapper;
import com.nhamt.book_store.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // The same with create private final UserRepository userRepo
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getAll(){
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).collect(Collectors.toList());
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}
