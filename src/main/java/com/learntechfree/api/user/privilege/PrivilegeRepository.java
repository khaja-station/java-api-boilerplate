package com.learntechfree.api.user.privilege;

import com.learntechfree.api.common.enums.PrivilegeType;
import com.learntechfree.api.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(PrivilegeType name);
}
