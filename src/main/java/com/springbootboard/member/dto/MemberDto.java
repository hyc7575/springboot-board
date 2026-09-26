package com.springbootboard.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberDto {
	private final Integer id;
	private final String email;
	private final String nickname;
}
