package com.springbootboard.board.post.controller;

import com.springbootboard.board.post.dto.PostCreateRequestDto;
import com.springbootboard.board.post.dto.PostDto;
import com.springbootboard.board.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostWebController {
    private final PostService postService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", postService.getPosts());
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("post", postService.getPost(id));
        return "posts/detail";
    }

    @GetMapping("/create")
    public String createForm() {
        return "posts/create";
    }

    @PostMapping
    public String create(@ModelAttribute PostCreateRequestDto dto, Authentication authentication) {
        PostDto post = postService.createPost(dto, authentication);
        return "redirect:/posts/" + post.getId();
    }
}
