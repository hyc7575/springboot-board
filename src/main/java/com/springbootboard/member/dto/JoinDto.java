package com.springbootboard.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record JoinDto (
	@NotBlank @Email String email,
	@NotBlank String password,
	@NotBlank String nickname
) {}
