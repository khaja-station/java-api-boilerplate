package com.khajastation.api.entity;

import com.khajastation.api.common.enums.PrivilegeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "privileges")
@NoArgsConstructor
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private PrivilegeType name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "privileges")
    private Collection<Role> roles;

    public Privilege(PrivilegeType name) {
        this.name = name;
    }
}

