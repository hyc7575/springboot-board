package com.springbootboard.board.post.domain;

import com.springbootboard.board.post.dto.PostDto;
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
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private Integer memberId;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private String content;
	@CreatedDate
  private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	public Post(Integer memberId, String title, String content) {
		this.memberId = memberId;
		this.title = title;
		this.content = content;
	}
	public void modify(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}

	public PostDto toDto() {
		return new PostDto(id, memberId, title, content, createdAt, deletedAt);
	}
}
