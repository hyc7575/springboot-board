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
		return commentRepository.findByPost_IdAndDeletedAtIsNullAndPost_DeletedAtIsNull(postId).stream()
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

	@Transactional
    public CommentDto updateComment(Integer postId, Integer commentId, Integer memberId, CommentUpdateRequestDto request) {
        Comment comment = findOwnedComment(postId, commentId, memberId);
        comment.modify(request.content());
        return CommentDto.from(comment);
    }

    @Transactional
    public void deleteComment(Integer postId, Integer commentId, Integer memberId) {
        Comment comment = findOwnedComment(postId, commentId, memberId);
        comment.delete();
    }

    private Comment findOwnedComment(Integer postId, Integer commentId, Integer memberId) {
        Comment comment = commentRepository.findByIdAndPost_IdAndDeletedAtIsNull(commentId, postId)
                .filter(found -> found.getPost().getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));
        if (!comment.getMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 수정하거나 삭제할 수 있습니다.");
        }
        return comment;
    }
}
