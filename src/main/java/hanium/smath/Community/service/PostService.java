// PostService.java
package hanium.smath.Community.service;

import hanium.smath.Community.dto.PostResponse;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public CompletableFuture<String> savePost(Post post) {
        return CompletableFuture.supplyAsync(() -> {
            Post savedPost = postRepository.save(post);
            return savedPost.getUpdatedTime().toString();
        });
    }

    public CompletableFuture<List<PostResponse>> getPostsByLoginId(String login_id) {
        return CompletableFuture.supplyAsync(() -> {
            List<Post> posts = postRepository.findByMemberLoginId(login_id);
            return posts.stream().map(post -> PostResponse.builder()
                            .id(post.getIdPost())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .postType(post.getPostType())
                            .createdTime(post.getCreatedTime().toString())
                            .updatedTime(post.getUpdatedTime().toString())
                            .build())
                    .collect(Collectors.toList());
        });
    }
}
