package hanium.smath.Community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
    private String comment_id;
    private String content;
    private String post_id;  // Post의 ID를 저장하는 필드
    private String login_id;  // Member의 ID를 저장하는 필드
    private String createTime;
    private String updateTime;
}
