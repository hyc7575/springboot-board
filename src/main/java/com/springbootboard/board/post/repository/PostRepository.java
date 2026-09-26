package com.springbootboard.board.post.repository;

import com.springbootboard.board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findAllByDeletedAtIsNull();

	Optional<Post> findByIdAndDeletedAtIsNull(int postId);
}
