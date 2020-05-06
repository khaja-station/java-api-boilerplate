package com.khajastation.api.user.auth;

import com.khajastation.api.entity.Member;
import com.khajastation.api.user.auth.dto.GuestInfoResponse;
import com.khajastation.api.user.auth.dto.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AuthMapper {
    GuestInfoResponse toGuestInfoResponse(Member member);

    SignUpRequest toSignUpRequest(Member member);
}