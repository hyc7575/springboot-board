package com.springbootboard.board.comment.dto;


public record CommentCreateRequestDto(
		String content, Integer parentId
) {

}