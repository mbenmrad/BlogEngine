package com.devskiller.tasks.blog.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.devskiller.tasks.blog.model.Comment;
import com.devskiller.tasks.blog.model.Post;
import com.devskiller.tasks.blog.model.dto.exception.ResourceNotFoundException;
import com.devskiller.tasks.blog.repository.CommentRepository;
import com.devskiller.tasks.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import com.devskiller.tasks.blog.model.dto.CommentDto;
import com.devskiller.tasks.blog.model.dto.NewCommentDto;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public CommentService(PostRepository postRepository, CommentRepository commentRepository) {
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
	}

	/**
	 * Returns a list of all comments for a blog post with passed id.
	 *
	 * @param postId id of the post
	 * @return list of comments sorted by creation date descending - most recent first
	 */
	public List<CommentDto> getCommentsForPost(Long postId) {

		Post post= postRepository.findById(postId).orElse(null);
		if(post == null|| post.getComments().isEmpty()){
			return Collections.emptyList();
		}

		return post.getComments().stream()
			.sorted(Comparator.comparing(Comment::getCreationDate).reversed())
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}

	/**
	 * Creates a new comment
	 *
	 * @param postId        id of the post
	 * @param newCommentDto data of new comment
	 * @return id of the created comment
	 * @throws IllegalArgumentException if postId is null or there is no blog post for passed postId
	 */
	public Long addComment(Long postId, NewCommentDto newCommentDto) {
		Post post= postRepository.findById(postId)
			.orElseThrow(() -> new ResourceNotFoundException("The post with id " + postId + "is not found"));

		Comment newComment = new Comment();
		newComment.setContent(newCommentDto.content());
		newComment.setAuthor(newCommentDto.author());
		newComment.setCreationDate(LocalDateTime.now());
		post.addComment(newComment);
		Comment savedComment = commentRepository.save(newComment);
		postRepository.save(post);

		return savedComment.getId();
	}


	private CommentDto convertToDto(Comment comment) {
		return  new CommentDto(comment.getId(), comment.getContent(), comment.getAuthor(), comment.getCreationDate());
	}

}
