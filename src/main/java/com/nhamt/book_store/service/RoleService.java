package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.request.PermissionRequest;
import com.nhamt.book_store.dto.request.RoleRequest;
import com.nhamt.book_store.dto.response.PermissionResponse;
import com.nhamt.book_store.dto.response.RoleResponse;
import com.nhamt.book_store.entity.Permission;
import com.nhamt.book_store.entity.Role;
import com.nhamt.book_store.mapper.PermissionMapper;
import com.nhamt.book_store.mapper.RoleMapper;
import com.nhamt.book_store.repository.PermissionRepository;
import com.nhamt.book_store.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // The same with create private final UserRepository userRepo
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll(){
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
    }

    public void delete(String role){
        roleRepository.deleteById(role);
    }
}
