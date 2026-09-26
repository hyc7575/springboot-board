package com.springbootboard.board.comment.repository;

import com.springbootboard.board.comment.domain.Comment;
import com.springbootboard.board.comment.dto.CommentDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	List<CommentDto> findByPostId(Long postId);
}
