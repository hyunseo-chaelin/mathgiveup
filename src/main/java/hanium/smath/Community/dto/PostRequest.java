package hanium.smath.Community.dto;

import hanium.smath.Community.entity.PostType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    private String title;
    private String content;
    private PostType postType;
}
