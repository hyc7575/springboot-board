package com.springbootboard.board.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto {
	private final Integer id;
	private final Integer memberId;
	private final String title;
	private final String content;
	private final LocalDateTime createdAt;
	private final LocalDateTime deletedAt;
}
