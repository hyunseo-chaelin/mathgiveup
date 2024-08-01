//package hanium.smath.Community.repository;
//
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.QueryDocumentSnapshot;
//import com.google.cloud.firestore.QuerySnapshot;
//import hanium.smath.Community.entity.Comment;
//import hanium.smath.Community.util.DateUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.stream.Collectors;
//
//@Repository
//public class CommentRepository {
//
//    @Autowired
//    private Firestore firestore;
//
//    private static final String COLLECTION_NAME = "Comments";
//
//    public CompletableFuture<String> saveComment(Comment comment) {
//        System.out.println("Saving comment: " + comment);
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                comment.setCreatedAt(DateUtils.formatLocalDateTime(LocalDateTime.now()));
//                comment.setUpdatedAt(DateUtils.formatLocalDateTime(LocalDateTime.now()));
//                firestore.collection(COLLECTION_NAME).document(comment.getId()).set(comment).get();
//                String updateTime = firestore.collection(COLLECTION_NAME).document(comment.getId()).get().get().getUpdateTime().toString();
//                System.out.println("Comment saved with updateTime: " + updateTime);
//                return updateTime;
//            } catch (InterruptedException | ExecutionException e) {
//                System.err.println("Error saving comment: " + e.getMessage());
//                throw new RuntimeException(e);
//            }
//        }).exceptionally(ex -> {
//            System.err.println("Error in saveComment: " + ex.getMessage());
//            throw new RuntimeException(ex);
//        });
//    }
//
//    public CompletableFuture<List<Comment>> getCommentsByMemberId(String idMember) {
//        System.out.println("Finding comments for idMember: " + idMember);
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                DocumentReference memberRef = firestore.collection("Members").document(idMember);
//                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
//                        .whereEqualTo("idMember", memberRef)
//                        .get();
//
//                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//                System.out.println("Found " + documents.size() + " comments for idMember: " + idMember);
//
//                List<Comment> comments = documents.stream()
//                        .map(doc -> doc.toObject(Comment.class))
//                        .collect(Collectors.toList());
//
//                return comments;
//            } catch (InterruptedException | ExecutionException e) {
//                System.err.println("Error fetching comments: " + e.getMessage());
//                throw new RuntimeException(e);
//            }
//        }).exceptionally(ex -> {
//            System.err.println("Error in getCommentsByidMember: " + ex.getMessage());
//            throw new RuntimeException(ex);
//        });
//    }
//
//    public CompletableFuture<List<Comment>> getCommentsByPostId(String idPost) {
//        System.out.println("Finding comments for idPost: " + idPost);
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                DocumentReference postRef = firestore.collection("Posts").document(idPost);
//                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
//                        .whereEqualTo("idPost", postRef)
//                        .get();
//
//                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//                System.out.println("Found " + documents.size() + " comments for idPost: " + idPost);
//
//                List<Comment> comments = documents.stream()
//                        .map(doc -> doc.toObject(Comment.class))
//                        .collect(Collectors.toList());
//
//                return comments;
//            } catch (InterruptedException | ExecutionException e) {
//                System.err.println("Error fetching comments: " + e.getMessage());
//                throw new RuntimeException(e);
//            }
//        }).exceptionally(ex -> {
//            System.err.println("Error in getCommentsByPostId: " + ex.getMessage());
//            throw new RuntimeException(ex);
//        });
//    }
//}
