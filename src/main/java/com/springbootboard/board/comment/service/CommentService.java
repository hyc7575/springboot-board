package com.springbootboard.board.comment.service;

import com.springbootboard.board.comment.domain.Comment;
import com.springbootboard.board.comment.dto.CommentCreateRequestDto;
import com.springbootboard.board.comment.dto.CommentDto;
import com.springbootboard.board.comment.dto.CommentUpdateRequestDto;
import com.springbootboard.board.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import com.springbootboard.board.post.domain.Post;
import com.springbootboard.board.post.repository.PostRepository;
import com.springbootboard.member.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	@Transactional(readOnly = true)
	public List<CommentDto> getComments(Integer postId) {
		return commentRepository.findByPostId(postId).stream()
				.map(CommentDto::from)
				.toList();
	}

	@Transactional
	public CommentDto createComment(Integer postId, Integer memberId, CommentCreateRequestDto request) {
		Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
		memberRepository.findById(memberId)
				.filter(member -> member.getDeletedAt() == null)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효한 회원이 아닙니다."));

		Comment parent = null;
		if (request.parentId() != null) {
			parent = commentRepository.findByIdAndPost_IdAndDeletedAtIsNull(request.parentId(), postId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글의 부모 댓글을 찾을 수 없습니다."));
		}
		Comment comment = new Comment(post, memberId, request.content(), parent);
		return CommentDto.from(commentRepository.save(comment));
	}

	public CommentDto updateComment(Integer postId, Long commentId, CommentUpdateRequestDto request) {
		return null;
	}

	public void deleteComment(Integer postId, Long commentId) {
	}
}
