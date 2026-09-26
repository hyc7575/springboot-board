package com.springbootboard.global;

import com.springbootboard.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	private final JwtEncoder jwtEncoder;

	public String createAccessToken(Member member) {

		Instant now = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(member.getId().toString())
				.issuedAt(now)
				.expiresAt(now.plusSeconds(60 * 60))
				.claim("email", member.getEmail())
				.build();

		return jwtEncoder
				.encode(JwtEncoderParameters.from(claims))
				.getTokenValue();
	}
}