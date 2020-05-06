package com.khajastation.api.user.role;

import com.khajastation.api.common.enums.RoleType;
import com.khajastation.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);

    Boolean existsByName(RoleType name);
}
