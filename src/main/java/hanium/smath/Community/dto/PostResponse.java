package hanium.smath.Community.dto;

import com.google.cloud.firestore.DocumentReference;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private DocumentReference idMember;
    private String createdAt;
    private String updatedAt;
}
