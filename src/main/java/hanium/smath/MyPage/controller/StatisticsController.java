//package hanium.smath.MyPage.controller;
//
//import hanium.smath.MyPage.dto.DailyStatisticsResponse;
//import hanium.smath.MyPage.service.StatisticsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.concurrent.CompletableFuture;
//
//@RestController
//@RequestMapping("/api/statistics")
//public class StatisticsController {
//
//    @Autowired
//    private StatisticsService statisticsService;
//
//    @GetMapping("/{idMember}/date/{date}")
//    public CompletableFuture<ResponseEntity<DailyStatisticsResponse>> getDailyStatistics(
//            @PathVariable String idMember,
//            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//
//        System.out.println("Received request for daily statistics. User ID: " + idMember + ", Date: " + date);
//
//        return statisticsService.getDailyStatistics(idMember, date)
//                .thenApply(response -> {
//                    System.out.println("Returning response for user: " + idMember + " on date: " + date);
//                    return ResponseEntity.ok(response);
//                });
//    }
//}
