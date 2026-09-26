package com.springbootboard.board.comment.controller;

import com.springbootboard.board.comment.dto.CommentCreateRequestDto;
import com.springbootboard.board.comment.dto.CommentDto;
import com.springbootboard.board.comment.dto.CommentUpdateRequestDto;
import com.springbootboard.board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import com.springbootboard.global.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentApiController {

	private final CommentService commentService;

	@GetMapping
	public ApiResponse<List<CommentDto>> getComments(
			@PathVariable Integer postId
	) {
		return ApiResponse.success(commentService.getComments(postId));
	}

	@PostMapping
	public ApiResponse<CommentDto> createComment(
			@PathVariable Integer postId,
			@Valid @RequestBody CommentCreateRequestDto request,
			@AuthenticationPrincipal Jwt jwt
	) {
		if (jwt == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		Integer memberId;
		try {
			memberId = Integer.valueOf(jwt.getSubject());
		} catch (NumberFormatException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 회원 식별자입니다.");
		}
		return ApiResponse.success(commentService.createComment(postId, memberId, request));
	}

	@PatchMapping("/{commentId}")
	public ApiResponse<CommentDto> updateComment(
			@PathVariable Integer postId,
			@PathVariable Long commentId,
			@RequestBody CommentUpdateRequestDto request
	) {
		return ApiResponse.success(commentService.updateComment(postId, commentId, request));
	}

	@DeleteMapping("/{commentId}")
	public ApiResponse<Void> deleteComment(
			@PathVariable Integer postId,
			@PathVariable Long commentId
	) {
		commentService.deleteComment(postId, commentId);
		return ApiResponse.success(null);
	}
}