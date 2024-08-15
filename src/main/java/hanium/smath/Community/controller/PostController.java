package hanium.smath.Community.controller;

import hanium.smath.Community.dto.PostRequest;
import hanium.smath.Community.dto.PostResponse;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.service.PostService;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.Member.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;
    private final LoginRepository loginRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public PostController(PostService postService, LoginRepository loginRepository, JwtUtil jwtUtil) {
        this.postService = postService;
        this.loginRepository = loginRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/my")
    public CompletableFuture<ResponseEntity<List<PostResponse>>> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
        String loginId = userDetails.getUsername();
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
    public CompletableFuture<ResponseEntity<String>> createPost(@RequestBody PostRequest postRequest, Authentication authentication) {
        String loginId = authentication.getName();
        Member member = loginRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .postType(postRequest.getPostType())
                .member(member)
                .build();

        return postService.savePost(post)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).build());
    }

    @PatchMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> updatePost(@PathVariable("id") Long postId, @RequestBody PostRequest postRequest, Authentication authentication) {
        String loginId = authentication.getName();
        return postService.updatePost(postId, postRequest, loginId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).build());
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deletePost(@PathVariable("id") Long postId, Authentication authentication) {
        String loginId = authentication.getName();
        return postService.deletePost(postId, loginId)
                .thenApply(v -> ResponseEntity.noContent().<Void>build())  // <Void> 명시적으로 추가
                .exceptionally(ex -> {
                    System.err.println("Error in deletePost: " + ex.getMessage());
                    return ResponseEntity.status(500).<Void>build();  // <Void> 명시적으로 추가
                });
    }
}