package hanium.smath.MyPage.controller;

import hanium.smath.MyPage.dto.LearningRecordResponse;
import hanium.smath.MyPage.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    @Autowired
    private LearningService service;

    @GetMapping("/records/monthly")
    public Mono<LearningRecordResponse> getMonthlyLearningRecords(@RequestParam String login_id, @RequestParam String yearMonth) {
        return service.getMonthlyLearningRecords(login_id, yearMonth);
    }
}
