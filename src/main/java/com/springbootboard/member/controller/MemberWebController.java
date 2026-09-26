package com.springbootboard.member.controller;

import com.springbootboard.member.dto.JoinDto;
import com.springbootboard.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberWebController {
	private final AuthService authService;
	@GetMapping("/login")
	public String loignPage() {
		return "login";
	}

	@GetMapping("/join")
	public String joinPage() {
		return "join";
	}

	@PostMapping("/join")
	public String join(@RequestBody JoinDto jDto) {
		authService.join(jDto);

		return "redirect:/posts";
	}
}
