package com.springbootboard.board.post.service;

import com.springbootboard.board.post.domain.Post;
import com.springbootboard.board.post.dto.PostCreateRequestDto;
import com.springbootboard.board.post.dto.PostDto;
import com.springbootboard.board.post.repository.PostRepository;
import com.springbootboard.member.domain.Member;
import com.springbootboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	public List<PostDto> getPosts() {
		List<Post> posts = postRepository.findAllByDeletedAtIsNull();
		return posts.stream().map(Post::toDto).toList();
	}
	public PostDto getPost(int postId) {
		Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"게시글을 찾을 수 없습니다."
				));

		return post.toDto();
	}
	public PostDto createPost(
			PostCreateRequestDto dto,
			@AuthenticationPrincipal Jwt jwt
	) {
		Integer memberId = Integer.valueOf(jwt.getSubject());
		System.out.println("---- create post ---- %d".formatted(memberId));
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."
				));
		Post post = new Post(member.getId(), dto.title(), dto.content());
		postRepository.save(post);
		return post.toDto();
	}
}

