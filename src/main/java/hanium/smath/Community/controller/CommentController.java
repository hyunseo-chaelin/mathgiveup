package hanium.smath.Community.controller;

import hanium.smath.Community.dto.CommentResponse;
import hanium.smath.Community.entity.Comment;
import hanium.smath.Community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/community/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/my")
    public CompletableFuture<ResponseEntity<List<CommentResponse>>> getMyComments(Authentication authentication) {
        String idMember = authentication.getName(); // Firebase 인증된 UID 사용
        System.out.println("Retrieving comments for memberId: " + idMember);

        return commentService.getCommentsByMemberId(idMember)
                .thenApply(comments -> ResponseEntity.ok(comments))
                .exceptionally(ex -> {
                    System.err.println("Error in getMyComments: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createComment(@RequestBody Comment comment, Authentication authentication) {
        String memberId = authentication.getName(); // Firebase 인증된 UID 사용
        String postId = comment.getIdPost().getId(); // 클라이언트가 보내준 댓글에 있는 postId 사용
        System.out.println("Creating comment: " + comment);
        return commentService.saveComment(comment, memberId, postId)
                .thenApply(updateTime -> ResponseEntity.ok(updateTime))
                .exceptionally(ex -> {
                    System.err.println("Error in createComment: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    @GetMapping("/post/{idPost}")
    public CompletableFuture<ResponseEntity<List<CommentResponse>>> getCommentsByPostId(@PathVariable String idPost) {
        System.out.println("Retrieving comments for postId: " + idPost);
        return commentService.getCommentsByPostId(idPost)
                .thenApply(comments -> ResponseEntity.ok(comments))
                .exceptionally(ex -> {
                    System.err.println("Error in getCommentsByPostId: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }
}
