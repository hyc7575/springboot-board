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
		List<Post> posts = postRepository.findAllByDeletedAtIsNotNull();
		System.out.println("--- post ---");
		log.debug(posts.toString());
		return posts.stream().map(Post::toDto).toList();
	}
	public PostDto getPost(int postId) {
		Post post = (Post) postRepository.findByIdAndDeletedAtIsNull(postId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"게시글을 찾을 수 없습니다."
				));

		return post.toDto();
	}
	public PostDto createPost(PostCreateRequestDto dto, Authentication authentication) {
		log.debug("email : {}", authentication.getName());
		Member member = memberRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."
				));
		Post post = new Post(member.getId(), dto.title(), dto.content());
		postRepository.save(post);
		return post.toDto();
	}
}

