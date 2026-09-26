package com.springbootboard.board.post.controller;

import com.springbootboard.board.post.dto.PostCreateRequestDto;
import com.springbootboard.board.post.dto.PostDto;
import com.springbootboard.board.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostApiController {
	private final PostService postService;
	@GetMapping
	public List<PostDto> list() {
		return postService.getPosts();
	}

	@GetMapping("/{id}")
	public PostDto detail(@PathVariable Integer id) {
		return postService.getPost(id);
	}

	@PostMapping
	public PostDto create(@RequestBody PostCreateRequestDto dto, Authentication authentication) {
		System.out.println("--- post create api ---");
		return postService.createPost(dto, authentication);
	}
}