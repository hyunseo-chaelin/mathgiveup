package hanium.smath.Community.dto;

import hanium.smath.Community.entity.PostType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private PostType postType;
    private String createdTime;
    private String updatedTime;
}
