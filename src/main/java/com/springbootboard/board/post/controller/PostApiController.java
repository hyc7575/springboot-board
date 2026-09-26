package com.springbootboard.board.post.controller;

import com.springbootboard.board.post.dto.PostCreateRequestDto;
import com.springbootboard.board.post.dto.PostDto;
import com.springbootboard.board.post.service.PostService;
import lombok.RequiredArgsConstructor;
import com.springbootboard.global.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostApiController {
	private final PostService postService;
	@GetMapping
	public ApiResponse<List<PostDto>> list() {
		return ApiResponse.success(postService.getPosts());
	}

	@GetMapping("/{id}")
	public ApiResponse<PostDto> detail(@PathVariable Integer id) {
		return ApiResponse.success(postService.getPost(id));
	}

	@PostMapping
	public ApiResponse<PostDto> create(@RequestBody PostCreateRequestDto dto, Authentication authentication) {
		System.out.println("--- post create api ---");
		return ApiResponse.success(postService.createPost(dto, authentication));
	}
}