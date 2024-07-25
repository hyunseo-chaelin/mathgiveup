package hanium.smath.Community.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class PostRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "Posts";

    public CompletableFuture<String> savePost(Post post) {
        System.out.println("Saving post: " + post);
        return CompletableFuture.supplyAsync(() -> {
            try {
                post.setCreatedAt(DateUtils.formatLocalDateTime(LocalDateTime.now()));
                post.setUpdatedAt(DateUtils.formatLocalDateTime(LocalDateTime.now()));
                firestore.collection(COLLECTION_NAME).document(post.getId()).set(post).get();
                String updateTime = firestore.collection(COLLECTION_NAME).document(post.getId()).get().get().getUpdateTime().toString();
                System.out.println("Post saved with updateTime: " + updateTime);
                return updateTime;
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error saving post: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
            System.err.println("Error in savePost: " + ex.getMessage());
            throw new RuntimeException(ex);
        });
    }

    public CompletableFuture<List<Post>> getPostsByMemberId(String idMember) {
        System.out.println("Finding posts for idMember: " + idMember);
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference memberRef = firestore.collection("Members").document(idMember);
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("idMember", idMember)
                        .get();

                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                System.out.println("Found " + documents.size() + " posts for idMember: " + idMember);

                List<Post> posts = documents.stream()
                        .map(doc -> doc.toObject(Post.class))
                        .collect(Collectors.toList());

                return posts;
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error fetching posts: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
            System.err.println("Error in getPostsByidMember: " + ex.getMessage());
            throw new RuntimeException(ex);
        });
    }
}
