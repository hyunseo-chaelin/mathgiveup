package hanium.smath.Community.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
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
    private final Firestore firestore;


//    @Autowired
//    public PostService(PostRepository postRepository) {
//        this.postRepository = postRepository;
//    }

    @Autowired
    public PostService(PostRepository postRepository, Firestore firestore) {
        this.postRepository = postRepository;
        this.firestore = firestore;
    }


//    public CompletableFuture<String> savePost(Post post) {
//        return postRepository.savePost(post)
//                .exceptionally(ex -> {
//                    System.err.println("Error in savePost: " + ex.getMessage());
//                    throw new RuntimeException(ex);
//                });
//    }

    public CompletableFuture<String> savePost(Post post) {
        DocumentReference loginIdRef  = firestore.collection("Members").document(post.getLogin_id().getId());
        post.setLogin_id(loginIdRef);
        return postRepository.savePost(post)
                .exceptionally(ex -> {
                    System.err.println("Error in savePost: " + ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }



    public CompletableFuture<List<PostResponse>> getPostsByLoginId(String login_id) {
        DocumentReference loginIdRef = firestore.collection("Members").document(login_id);
        return postRepository.getPostsByLoginId(loginIdRef)
                .thenApply(posts -> posts.stream().map(post -> PostResponse.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .login_id(post.getLogin_id())
                                .createdAt(post.getCreatedAt())
                                .updatedAt(post.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .exceptionally(ex -> {
                    System.err.println("Error in getPostsByMemberId: " + ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

//    public CompletableFuture<List<PostResponse>> getPostsByMemberId(String idMember) {
//        // idMember를 DocumentReference로 변환하여 전달
//        DocumentReference idMemberRef = firestore.collection("Members").document(idMember);
//        return postRepository.getPostsByMemberId(idMemberRef)
//                .thenApply(posts -> posts.stream().map(post -> PostResponse.builder()
//                                .id(post.getId())
//                                .title(post.getTitle())
//                                .content(post.getContent())
//                                .idMember(post.getIdMember())
//                                .createdAt(post.getCreatedAt())
//                                .updatedAt(post.getUpdatedAt())
//                                .build())
//                        .collect(Collectors.toList()))
//                .exceptionally(ex -> {
//                    System.err.println("Error in getPostsByMemberId: " + ex.getMessage());
//                    throw new RuntimeException(ex);
//                });
//    }

//    public CompletableFuture<List<PostResponse>> getPostsByMemberId(String idMember) {
//        return postRepository.getPostsByMemberId(idMember)
//                .thenApply(posts -> posts.stream().map(post -> PostResponse.builder()
//                                .id(post.getId())
//                                .title(post.getTitle())
//                                .content(post.getContent())
//                                .idMember(post.getIdMember())
//                                .createdAt(post.getCreatedAt())
//                                .updatedAt(post.getUpdatedAt())
//                                .build())
//                        .collect(Collectors.toList()))
//                .exceptionally(ex -> {
//                    System.err.println("Error in getPostsByMemberId: " + ex.getMessage());
//                    throw new RuntimeException(ex);
//                });
//    }
}
