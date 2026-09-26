package com.springbootboard.board.comment.controller;

import com.springbootboard.board.comment.dto.CommentCreateRequestDto;
import com.springbootboard.board.comment.dto.CommentDto;
import com.springbootboard.board.comment.dto.CommentUpdateRequestDto;
import com.springbootboard.board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

	private final CommentService commentService;

	@GetMapping
	public List<CommentDto> getComments(
			@PathVariable Long postId
	) {
		return commentService.getComments(postId);
	}

	@PostMapping
	public CommentDto createComment(
			@PathVariable Long postId,
			@RequestBody CommentCreateRequestDto request
	) {
		return commentService.createComment(postId, request);
	}

	@PatchMapping("/{commentId}")
	public CommentDto updateComment(
			@PathVariable Long postId,
			@PathVariable Long commentId,
			@RequestBody CommentUpdateRequestDto request
	) {
		return commentService.updateComment(postId, commentId, request);
	}

	@DeleteMapping("/{commentId}")
	public void deleteComment(
			@PathVariable Long postId,
			@PathVariable Long commentId
	) {
		commentService.deleteComment(postId, commentId);
	}
}