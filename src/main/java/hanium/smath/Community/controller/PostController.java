package hanium.smath.Community.controller;

import hanium.smath.Community.dto.PostResponse;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.service.PostService;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;
    private final LoginRepository loginRepository;

    @Autowired
    public PostController(PostService postService, LoginRepository loginRepository) {
        this.postService = postService;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/my")
    public CompletableFuture<ResponseEntity<List<PostResponse>>> getMyPosts(Authentication authentication) {
        if (authentication == null) {
            System.out.println("Authentication object is null");
            return CompletableFuture.completedFuture(ResponseEntity.status(403).build());
        }
        String loginId = authentication.getName();
        System.out.println("Retrieving posts for loginId: " + loginId);
        return postService.getPostsByLoginId(loginId)
                .thenApply(posts -> {
                    System.out.println("Posts retrieved: " + posts.size());
                    return ResponseEntity.ok(posts);
                })
                .exceptionally(ex -> {
                    System.err.println("Error in getMyPosts: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createPost(@RequestBody Post post, Authentication authentication) {
        if (authentication == null) {
            System.out.println("Authentication object is null");
            return CompletableFuture.completedFuture(ResponseEntity.status(403).build());
        }

        String loginId = authentication.getName();
        Optional<Member> optionalMember = loginRepository.findByLoginId(loginId);
        if (optionalMember.isEmpty()) {
            System.out.println("Member not found for loginId: " + loginId);
            return CompletableFuture.completedFuture(ResponseEntity.status(404).build());
        }
        post.setMember(optionalMember.get()); // Ensure the member is set

        System.out.println("Post object received: " + post);
        System.out.println("PostType: " + post.getPostType());
        System.out.println("Title: " + post.getTitle());
        System.out.println("Content: " + post.getContent());

        System.out.println("Creating post: " + post);
        return postService.savePost(post)
                .thenApply(updateTime -> ResponseEntity.ok(updateTime))
                .exceptionally(ex -> {
                    System.err.println("Error in createPost: " + ex.getMessage());
                    return ResponseEntity.status(500).build();
                });
    }
}
