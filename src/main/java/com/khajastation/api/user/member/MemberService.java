package com.khajastation.api.user.member;

import com.khajastation.api.common.enums.AuthProvider;
import com.khajastation.api.common.enums.ContactType;
import com.khajastation.api.common.enums.RoleType;
import com.khajastation.api.common.exception.ResourceNotFoundException;
import com.khajastation.api.entity.Member;
import com.khajastation.api.entity.Role;
import com.khajastation.api.user.verification.ContactVerificationService;
import com.khajastation.api.user.auth.dto.Oauth2SignupRequest;
import com.khajastation.api.user.role.RoleService;
import com.khajastation.api.user.auth.dto.SignUpRequest;
import com.khajastation.api.user.member.dto.MemberResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ContactVerificationService contactVerificationService;

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
    }

    public Member findByReferenceId(UUID referenceId) {
        return memberRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", referenceId));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", email));
    }

    public Boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Boolean isPhoneDuplicate(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }

    public Member create(SignUpRequest signUpRequest) {
        Role roleUser = roleService.findByName(RoleType.ROLE_USER);
        Member member = memberMapper.toMember(signUpRequest);

        member.setProvider(AuthProvider.SYSTEM);
        member.setReferenceId(generateReferenceId());
        member.setRoles(new ArrayList<>(Collections.singletonList(roleUser)));
        member.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        return save(member);
    }

    public Member updateOauth2Member(Oauth2SignupRequest oauth2SignupRequest, Member member) {
        Role roleUser = roleService.findByName(RoleType.ROLE_USER);
        member.setPhoneNumber(oauth2SignupRequest.getPhoneNumber());
        member.setRoles(new ArrayList<>(Collections.singletonList(roleUser)));

        return save(member);
    }

    public Member save(Member sender) {
        return memberRepository.save(sender);
    }

    public MemberResponse getCurrentMember(String email) {
        Member member = findByEmail(email);

        return memberMapper.toMemberResponse(member);
    }

    public List<MemberResponse> getLockedSenders() {
        List<Member> senders = memberRepository.findAllByLocked(true);

        return memberMapper.toMemberResponseList(senders);
    }

    @Transactional
    public void unLock(UUID referenceId) {
        Member member = findByReferenceId(referenceId);

        if (!member.isEmailVerified())
            contactVerificationService.resetResendAttempts(member, ContactType.EMAIL);

        if (!member.isPhoneNumberVerified())
            contactVerificationService.resetResendAttempts(member, ContactType.PHONE);

        member.setLoginAttempts(0);
        member.setLocked(false);
        memberRepository.save(member);
    }

    private UUID generateReferenceId() {
        return UUID.randomUUID();
    }
}