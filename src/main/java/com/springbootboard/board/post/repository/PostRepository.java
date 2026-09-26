package com.springbootboard.board.post.repository;

import com.springbootboard.board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findAllByDeletedAtIsNotNull();

	<T> ScopedValue<T> findByIdAndDeletedAtIsNull(int postId);
}
