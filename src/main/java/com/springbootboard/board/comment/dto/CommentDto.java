package com.springbootboard.board.comment.dto;

import com.springbootboard.board.comment.domain.Comment;

public record CommentDto(
		Long id,
		Long memberId,
		String content,
		Long parentId
) {
	public static CommentDto from(Comment comment) {
		Integer parentId = comment.getParentComment() != null
				? comment.getParentComment().getId() : null;
		return new CommentDto(
				comment.getId() != null ? comment.getId().longValue() : null,
				comment.getMemberId() != null ? comment.getMemberId().longValue() : null,
				comment.getContent(),
				parentId != null ? parentId.longValue() : null
		);
	}
}
