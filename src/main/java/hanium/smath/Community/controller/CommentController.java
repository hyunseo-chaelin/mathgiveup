package hanium.smath.Community.controller;

import hanium.smath.Community.dto.CommentResponse;
import hanium.smath.Community.entity.Comment;
import hanium.smath.Community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
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
    public CompletableFuture<ResponseEntity<List<CommentResponse>>> getMyComments() {
        // JWT에서 인증된 사용자의 ID를 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName(); // JWT에서 가져온 사용자 ID

        System.out.println("Retrieving comments for memberId: " + memberId);

        return commentService.getCommentsByMemberId(memberId)
                .thenApply(comments -> ResponseEntity.ok(comments))
                .exceptionally(ex -> {
                    System.err.println("Error in getMyComments: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createComment(@RequestBody Map<String, Object> payload, Authentication authentication) {
        String memberId = authentication.getName(); // JWT에서 인증된 사용자 ID 가져오기
        String content = (String) payload.get("content"); // 클라이언트가 보낸 댓글 내용
        Long postId = ((Number) payload.get("postId")).longValue(); // 클라이언트가 보낸 게시물 ID

        // Comment 객체 생성
        Comment comment = Comment.builder()
                .content(content)
                .build();

        System.out.println("Creating comment: " + comment);
        return commentService.saveComment(comment, memberId, postId)
                .thenApply(updateTime -> ResponseEntity.ok(updateTime))
                .exceptionally(ex -> {
                    System.err.println("Error in createComment: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    @GetMapping("/post/{idPost}")
    public CompletableFuture<ResponseEntity<List<CommentResponse>>> getCommentsByPostId(@PathVariable Long idPost) {
        System.out.println("Retrieving comments for postId: " + idPost);
        return commentService.getCommentsByPostId(idPost)
                .thenApply(comments -> ResponseEntity.ok(comments))
                .exceptionally(ex -> {
                    System.err.println("Error in getCommentsByPostId: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }
}
