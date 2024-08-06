package hanium.smath.MyPage.controller;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.MyPage.entity.Achievement;
import hanium.smath.MyPage.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private LoginRepository loginRepository;

    @GetMapping("/{login_id}")
    public List<Achievement> getAchievements(@PathVariable String login_id) {
        Member member = loginRepository.findByLoginId(login_id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));

        return achievementService.getAchievements(member);
    }
}
