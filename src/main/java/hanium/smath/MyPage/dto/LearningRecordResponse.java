package hanium.smath.MyPage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class LearningRecordResponse {

    private String login_id;
    private String yearMonth;
    private List<String> learningDays;
    private long consecutiveDays;

    public LearningRecordResponse(String login_id, String yearMonth, List<String> learningDays, long consecutiveDays) {
        this.login_id = login_id;
        this.yearMonth = yearMonth;
        this.learningDays = learningDays;
        this.consecutiveDays = consecutiveDays;
    }
}
