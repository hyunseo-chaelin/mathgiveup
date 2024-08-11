package hanium.smath.MyPage.controller;

import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.MyPage.dto.AchievementResponse;
import hanium.smath.Member.security.JwtUtil;
import hanium.smath.MyPage.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private JwtUtil jwtUtil;

    // 7일 연속 학습 달성 관련 데이터를 반환하는 엔드포인트
    @GetMapping("/consecutive/7days")
    public ResponseEntity<AchievementResponse> getConsecutive7DayLearningAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        AchievementResponse response = achievementService.getConsecutiveLearningData(loginId, 7);

        return ResponseEntity.ok(response);
    }

    // 30일 연속 학습 달성 관련 데이터를 반환하는 엔드포인트
    @GetMapping("/consecutive/30days")
    public ResponseEntity<AchievementResponse> getConsecutive30DayLearningAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login_id = authentication.getName();

        AchievementResponse response = achievementService.getConsecutiveLearningData(login_id, 30);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public AchievementResponse getAllAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login_id = authentication.getName();

        return achievementService.getAllAchievements(login_id);
    }

}
