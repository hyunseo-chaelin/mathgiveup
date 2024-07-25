package hanium.smath.Community.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.firestore.DocumentReference;
import hanium.smath.Community.util.DocumentReferenceDeserializer;
import hanium.smath.Community.util.DocumentReferenceSerializer;
import lombok.*;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Post {
    private String id;
    private String title;
    private String content;

    @JsonSerialize(using = DocumentReferenceSerializer.class)
    @JsonDeserialize(using = DocumentReferenceDeserializer.class)
    private DocumentReference idMember;
    private String createdAt;
    private String updatedAt;
}
