package hanium.smath.MyPage.entity;

import com.google.cloud.firestore.DocumentReference;
import lombok. *;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LearningRecord {
    private String idRecord; // firestore에서 자동으로 생성하는 id
    private DocumentReference idMember; // Member 엔티티와의 연관성을 위한 필드 (Member 엔티티의 고유 식별자)
    private String learningDate; // 학습날짜
}
