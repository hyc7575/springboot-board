package com.springbootboard.board.comment.domain;

import com.springbootboard.board.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private String content;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;
	@Column(nullable = false)
	private Integer memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_coment_id")
	private Comment parentComment;

	@CreatedDate
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	public Comment(Post post, Integer memberId, String content) {
		this(post, memberId, content, null);
	}

	public Comment(Post post, Integer memberId, String content, Comment parentComment) {
		this.post = post;
		this.parentComment = parentComment;
		this.memberId = memberId;
		this.content = content;
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}
