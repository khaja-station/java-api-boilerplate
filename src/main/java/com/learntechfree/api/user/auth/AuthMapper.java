package com.learntechfree.api.user.auth;

import com.learntechfree.api.entity.Member;
import com.learntechfree.api.user.auth.dto.GuestInfoResponse;
import com.learntechfree.api.user.auth.dto.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AuthMapper {
    GuestInfoResponse toGuestInfoResponse(Member member);

    SignUpRequest toSignUpRequest(Member member);
}