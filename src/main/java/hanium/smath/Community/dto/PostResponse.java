package hanium.smath.Community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {  // 클래스는 여전히 public이어야 함
    private Long id;
    private String title;
    private String content;
//    private String postType;
    private String createdTime;
    private String updatedTime;

    // 필요하다면 명시적으로 생성자를 추가하고, public 접근 제어자를 설정합니다.
    public PostResponse(Long id, String title, String content, String createdTime, String updatedTime) {
        this.id = id;
        this.title = title;
        this.content = content;
//        this.postType = postType;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}
