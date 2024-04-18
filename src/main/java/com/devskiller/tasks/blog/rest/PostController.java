package com.devskiller.tasks.blog.rest;

import com.devskiller.tasks.blog.model.Comment;
import com.devskiller.tasks.blog.model.dto.CommentDto;
import com.devskiller.tasks.blog.model.dto.NewCommentDto;
import com.devskiller.tasks.blog.model.dto.exception.ResourceNotFoundException;
import com.devskiller.tasks.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.devskiller.tasks.blog.model.dto.PostDto;
import com.devskiller.tasks.blog.service.PostService;

import java.util.List;

@Controller
@RestController
@RequestMapping("/posts")
public class PostController {

	private final PostService postService;

	private final CommentService commentService;


	public PostController(PostService postService,  CommentService commentService) {
		this.postService = postService;
		this.commentService = commentService;
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public PostDto getPost(@PathVariable Long id) {
		return postService.getPost(id);
	}

	@PostMapping(value = "/{id}/comments")
	public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody NewCommentDto comment) {
		try {
			Long commentId = commentService.addComment(id, comment);
			return new ResponseEntity<>(commentId, HttpStatus.CREATED);
		} catch (ResourceNotFoundException e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@GetMapping(value = "/{id}/comments")
	public List<CommentDto> getCommentForPost(@PathVariable Long id) {
		return commentService.getCommentsForPost(id);
	}

}
