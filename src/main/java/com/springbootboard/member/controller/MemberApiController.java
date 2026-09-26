package com.springbootboard.member.controller;

import com.springbootboard.global.ApiResponse;
import com.springbootboard.member.dto.JoinDto;
import com.springbootboard.member.dto.LoginRequestDto;
import com.springbootboard.member.dto.LoginResponseDto;
import com.springbootboard.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(
        @RequestBody LoginRequestDto request
    ) {

        String token = memberService.login(
            request.email(),
            request.password()
        );

        return ApiResponse.success(
            new LoginResponseDto(token)
        );
    }

    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody JoinDto jDto) {
        String accessToken = memberService.join(jDto);

        return ApiResponse.success(
            "회원가입이 완료되었습니다.",
            accessToken
        );
    }
}
