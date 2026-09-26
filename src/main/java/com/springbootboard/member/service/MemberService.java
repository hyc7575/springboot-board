package com.springbootboard.member.service;

import com.springbootboard.global.JwtProvider;
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
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	@Transactional
	public String join(JoinDto jDto) {
		Member member = new Member(jDto.email(), passwordEncoder.encode(jDto.password()), jDto.nickname());

		if (memberRepository.existsByNicknameOrEmail(member.getNickname(), member.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임 또는 이메일입니다.");
		}
		memberRepository.save(member);

		return jwtProvider.createAccessToken(member);
	}

	@Transactional
	public String login(String email, String password) {
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException(
						"존재하지 않는 회원입니다."
				));

		if (!passwordEncoder.matches(
				password,
				member.getPassword()
		)) {
			throw new IllegalArgumentException(
					"비밀번호가 일치하지 않습니다."
			);
		}

		return jwtProvider.createAccessToken(member);
	}
}
