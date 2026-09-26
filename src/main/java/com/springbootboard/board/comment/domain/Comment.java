package com.springbootboard.board.comment.domain;

import com.springbootboard.board.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private String content;
	@Column(nullable = false)
	private Integer postId;
	@Column(nullable = false)
	private Integer memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_coment_id")
	private Comment parentComment;

	private LocalDateTime deletedAt;

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}
