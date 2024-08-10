package hanium.smath.Community.service;

import hanium.smath.Community.dto.CommentResponse;
import hanium.smath.Community.entity.Comment;
import hanium.smath.Community.entity.Post;
import hanium.smath.Community.repository.CommentRepository;
import hanium.smath.Community.repository.PostRepository;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final LoginRepository loginRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, LoginRepository loginRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.loginRepository = loginRepository;
        this.postRepository = postRepository;
    }

    public CompletableFuture<String> saveComment(Comment comment, String loginId, Long postId) {
        return CompletableFuture.supplyAsync(() -> {
            // loginId로 Member 조회
            Member member = loginRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            // postId로 Post 조회
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            comment.setMember(member);
            comment.setPost(post);
            comment.setCreatedTime(LocalDateTime.now());
            comment.setUpdatedTime(LocalDateTime.now());

            commentRepository.save(comment);
            return comment.getUpdatedTime().toString();
        });
    }

    public CompletableFuture<List<CommentResponse>> getCommentsByMemberId(String login_id) {
        return CompletableFuture.supplyAsync(() -> {
            Member member = loginRepository.findByLoginId(login_id)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            // Member로 댓글 조회
            List<Comment> comments = commentRepository.findByMember(member);

            // comment 엔티티를 commentResponse로 변환
            return comments.stream().map(comment -> CommentResponse.builder()
                    .comment_id(comment.getComment_id().toString())
                    .content(comment.getContent())
                    .post_id(comment.getPost().getIdPost().toString())
                    .login_id(comment.getMember().getLoginId())
                    .createTime(comment.getCreatedTime().toString())
                    .updateTime(comment.getUpdatedTime().toString())
                    .build()).collect(Collectors.toList());
        });
    }

    public CompletableFuture<List<CommentResponse>> getCommentsByPostId(Long post_id) {
        return CompletableFuture.supplyAsync(() -> {
            Post post = postRepository.findById(post_id).orElseThrow(() -> new RuntimeException("Post not found"));
            List<Comment> comments = commentRepository.findByPost(post);

            return comments.stream().map(comment -> CommentResponse.builder()
                    .comment_id(comment.getComment_id().toString())
                    .content(comment.getContent())
                    .post_id(comment.getPost().getIdPost().toString())
                    .login_id(comment.getMember().getLoginId())
                    .comment_id(comment.getCreatedTime().toString())
                    .updateTime(comment.getUpdatedTime().toString())
                    .build()).collect(Collectors.toList());
        });
    }
}
