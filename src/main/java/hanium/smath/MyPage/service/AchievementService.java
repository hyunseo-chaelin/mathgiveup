package hanium.smath.MyPage.service;

import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.MyPage.entity.Achievement;
import hanium.smath.MyPage.entity.AchievementType;
import hanium.smath.Member.entity.Member;
import hanium.smath.MyPage.repository.AchievementRepository;
import hanium.smath.MyPage.repository.AchievementTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private AchievementTypeRepository achievementTypeRepository;

    @Autowired
    private LoginRepository loginRepository;

    public void awardAchievementForConsecutiveLearningDays(String loginId, int consecutiveDays) {
        if (consecutiveDays >= 7) {
            AchievementType type = achievementTypeRepository.findById(1) // Assuming 1 is the ID for 7-day streak badge
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Achievement Type ID"));

            Member member = loginRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));

            Achievement achievement = Achievement.builder()
                    .member(member)
                    .achievementValue("7일 연속 학습 달성")
                    .createTime(LocalDateTime.now())
                    .achievementType(type)
                    .build();

            achievementRepository.save(achievement);
            System.out.println("Achievement awarded to member: " + loginId);
        }
    }

    public List<Achievement> getAchievements(Member member) {
        return achievementRepository.findByMember(member);
    }
}
