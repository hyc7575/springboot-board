package com.springbootboard.member.service;

import com.springbootboard.member.domain.Member;
import com.springbootboard.member.dto.JoinDto;
import com.springbootboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void join(JoinDto jDto) {
		Member member = new Member(jDto.email(), passwordEncoder.encode(jDto.password()), jDto.nickname());

		if (memberRepository.existsByNicknameOrEmail(member.getNickname(), member.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임 또는 이메일입니다.");
		}

		memberRepository.save(member);
	}
}
