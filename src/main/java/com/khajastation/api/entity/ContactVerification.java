package com.khajastation.api.entity;

import com.khajastation.api.common.enums.ContactType;
import com.khajastation.api.common.enums.ContactVerificationStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "contact_verifications")
public class ContactVerification extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private ContactType type;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactVerificationStatus status = ContactVerificationStatus.PENDING;

    @Column
    private Integer verificationAttempt;

    @Column
    private LocalDateTime expiryDate;

    @Column
    private LocalDateTime verifiedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private int resendAttempt;
}
