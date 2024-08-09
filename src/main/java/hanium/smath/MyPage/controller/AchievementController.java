package hanium.smath.MyPage.controller;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.MyPage.entity.Achievement;
import hanium.smath.MyPage.service.AchievementService;
import hanium.smath.Member.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Achievement> getAchievements() {
        // 현재 인증된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName(); // 인증된 사용자의 로그인 ID를 가져옴

        Member member = loginRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));

        return achievementService.getAchievements(member);
    }
}
