package com.khajastation.api.user.privilege;

import com.khajastation.api.common.enums.PrivilegeType;
import com.khajastation.api.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(PrivilegeType name);
}
