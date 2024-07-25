package hanium.smath.Community.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import hanium.smath.Community.dto.CommentResponse;
import hanium.smath.Community.entity.Comment;
import hanium.smath.Community.repository.CommentRepository;
import hanium.smath.Community.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final Firestore firestore;

    @Autowired
    public CommentService(CommentRepository commentRepository, Firestore firestore) {
        this.commentRepository = commentRepository;
        this.firestore = firestore;
    }

    public CompletableFuture<String> saveComment(Comment comment, String idMember, String idPost) {
        DocumentReference memberRef = firestore.collection("Members").document(comment.getIdMember().getId());
        DocumentReference postRef = firestore.collection("Posts").document(idPost);
        comment.setIdMember(memberRef);
        comment.setIdPost(postRef);

        comment.setCreatedAt(DateUtils.formatLocalDateTime(LocalDateTime.now()));
        comment.setUpdatedAt(DateUtils.formatLocalDateTime(LocalDateTime.now()));

        return commentRepository.saveComment(comment)
                .exceptionally(ex -> {
                    System.err.println("Error in saveComment: " + ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

    public CompletableFuture<List<CommentResponse>> getCommentsByMemberId(String idMember) {
        return commentRepository.getCommentsByMemberId(idMember)
                .thenApply(comments -> comments.stream().map(comment -> CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .idPost(comment.getIdPost())
                                .idMember(comment.getIdMember())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .exceptionally(ex -> {
                    System.err.println("Error in getCommentsByMemberId: " + ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

    public CompletableFuture<List<CommentResponse>> getCommentsByPostId(String idPost) {
        return commentRepository.getCommentsByPostId(idPost)
                .thenApply(comments -> comments.stream().map(comment -> CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .idPost(comment.getIdPost())
                                .idMember(comment.getIdMember())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .exceptionally(ex -> {
                    System.err.println("Error in getCommentsByidPost: " + ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }
}
