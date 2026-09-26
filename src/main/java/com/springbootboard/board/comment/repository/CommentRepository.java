package com.springbootboard.board.comment.repository;

import com.springbootboard.board.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	List<Comment> findByPost_IdAndDeletedAtIsNullAndPost_DeletedAtIsNull(Integer postId);
	Optional<Comment> findByIdAndPost_IdAndDeletedAtIsNull(Integer id, Integer postId);
}
