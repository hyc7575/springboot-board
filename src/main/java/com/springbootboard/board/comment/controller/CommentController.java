package com.springbootboard.board.comment.controller;

import com.springbootboard.board.comment.dto.CommentCreateRequestDto;
import com.springbootboard.board.comment.dto.CommentDto;
import com.springbootboard.board.comment.dto.CommentUpdateRequestDto;
import com.springbootboard.board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import com.springbootboard.global.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

	private final CommentService commentService;

	@GetMapping
	public ApiResponse<List<CommentDto>> getComments(
			@PathVariable Long postId
	) {
		return ApiResponse.success(commentService.getComments(postId));
	}

	@PostMapping
	public ApiResponse<CommentDto> createComment(
			@PathVariable Long postId,
			@RequestBody CommentCreateRequestDto request
	) {
		return ApiResponse.success(commentService.createComment(postId, request));
	}

	@PatchMapping("/{commentId}")
	public ApiResponse<CommentDto> updateComment(
			@PathVariable Long postId,
			@PathVariable Long commentId,
			@RequestBody CommentUpdateRequestDto request
	) {
		return ApiResponse.success(commentService.updateComment(postId, commentId, request));
	}

	@DeleteMapping("/{commentId}")
	public ApiResponse<Void> deleteComment(
			@PathVariable Long postId,
			@PathVariable Long commentId
	) {
		commentService.deleteComment(postId, commentId);
		return ApiResponse.success(null);
	}
}