package hanium.smath.MyPage.controller;

import hanium.smath.MyPage.dto.DailyStatisticsResponse;
import hanium.smath.MyPage.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/{login_id}/date/{date}")
    public CompletableFuture<ResponseEntity<DailyStatisticsResponse>> getDailyStatistics(
            @PathVariable String login_id,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        System.out.println("Received request for daily statistics. User ID: " + login_id + ", Date: " + date);

        return statisticsService.getDailyStatistics(login_id, date)
                .thenApply(response -> {
                    System.out.println("Returning response for user: " + login_id + " on date: " + date);
                    return ResponseEntity.ok(response);
                });
    }
}
