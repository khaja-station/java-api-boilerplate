package com.khajastation.api.user.member;

import com.khajastation.api.entity.Member;
import com.khajastation.api.user.member.dto.MemberResponse;
import com.khajastation.api.user.auth.dto.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MemberMapper {
    Member toMember(SignUpRequest signUpRequest);

    MemberResponse toMemberResponse(Member member);

    List<MemberResponse> toMemberResponseList(List<Member> members);
}