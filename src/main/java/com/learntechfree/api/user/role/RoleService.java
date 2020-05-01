package com.learntechfree.api.user.role;

import com.learntechfree.api.common.enums.RoleType;
import com.learntechfree.api.common.exception.ResourceNotFoundException;
import com.learntechfree.api.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findByName(RoleType role) {
        return roleRepository.findByName(role).orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
    }

    public Boolean existsByName(RoleType role) {
        return roleRepository.existsByName(role);
    }
}
