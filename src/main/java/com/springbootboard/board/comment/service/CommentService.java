package com.springbootboard.board.comment.service;

import com.springbootboard.board.comment.dto.CommentCreateRequestDto;
import com.springbootboard.board.comment.dto.CommentDto;
import com.springbootboard.board.comment.dto.CommentUpdateRequestDto;
import com.springbootboard.board.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	public List<CommentDto> getComments(Long postId) {
		List<CommentDto> comments = commentRepository.findByPostId(postId);
		return comments;
	}

	public CommentDto createComment(Long postId, CommentCreateRequestDto request) {

		return null;
	}

	public CommentDto updateComment(Long postId, Long commentId, CommentUpdateRequestDto request) {
		return null;
	}

	public void deleteComment(Long postId, Long commentId) {
	}
}
