package com.springbootboard.board.comment.dto;


import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequestDto(
		@NotBlank String content, Integer parentId
) {

}