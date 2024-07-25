package hanium.smath.Community.controller;

import hanium.smath.Community.dto.PostResponse;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/my")
    public CompletableFuture<ResponseEntity<List<PostResponse>>> getMyPosts(Authentication authentication) {
        String idMember = authentication.getName(); // Firebase 인증된 UID 사용
        System.out.println("Retrieving posts for idMember: " + idMember);
        return postService.getPostsByMemberId(idMember)
                .thenApply(posts -> ResponseEntity.ok(posts))
                .exceptionally(ex -> {
                    System.err.println("Error in getMyPosts: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createPost(@RequestBody Post post) {
        System.out.println("Creating post: " + post);
        return postService.savePost(post)
                .thenApply(updateTime -> ResponseEntity.ok(updateTime));
    }
}
