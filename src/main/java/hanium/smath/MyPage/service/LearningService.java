package hanium.smath.MyPage.service;

import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.MyPage.dto.LearningRecordResponse;
import hanium.smath.MyPage.entity.LearningRecord;
import hanium.smath.Member.entity.Member;
import hanium.smath.MyPage.repository.LearningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningService {

    @Autowired
    private LearningRecordRepository repository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private AchievementService achievementService;

    public Mono<LearningRecordResponse> getMonthlyLearningRecords(String loginId, String yearMonth) {
        try {
            System.out.println("Fetching monthly learning records for loginId: " + loginId + ", yearMonth: " + yearMonth);

            YearMonth ym = YearMonth.parse(yearMonth);
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();

            Member member = loginRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));

            // 수정 부분: 이전 달부터 데이터를 가져옴
            LocalDate previousMonthStartDate = startDate.minusMonths(1).withDayOfMonth(1);
            List<LearningRecord> records = repository.findByMemberAndLearningDateBetween(member, previousMonthStartDate, endDate);

            // 월 단위로 필터링
            List<String> learningDays = records.stream()
                    .filter(record -> !record.getLearningDate().isBefore(startDate) && !record.getLearningDate().isAfter(endDate))
                    .map(record -> record.getLearningDate().toString())
                    .collect(Collectors.toList());

            System.out.println("Learning days retrieved: " + learningDays);

            // 수정 부분: 전체 기간의 연속 학습일 계산
            long consecutiveDays = calculateConsecutiveLearningDays(records);

            System.out.println("Consecutive learning days: " + consecutiveDays);


            // 7일 연속 학습 시 뱃지 수여 로직 추가
            achievementService.awardAchievementForConsecutiveLearningDays(loginId, (int) consecutiveDays);

            LearningRecordResponse response = new LearningRecordResponse(loginId, yearMonth, learningDays, consecutiveDays);
            return Mono.just(response);
        } catch (Exception e) {
            System.err.println("Error fetching learning records: " + e.getMessage());
            return Mono.error(e);
        }
    }

    // 수정 부분: 연속 학습일 계산 로직 수정
    private long calculateConsecutiveLearningDays(List<LearningRecord> records) {
        records.sort((record1, record2) -> record1.getLearningDate().compareTo(record2.getLearningDate()));

        System.out.println("Sorted records by learningDate: " + records);

        long maxConsecutiveDays = 0;
        long currentStreak = 0;
        LocalDate lastDate = null;

        for (LearningRecord record : records) {
            LocalDate learningDate = record.getLearningDate();

            if (lastDate != null && !learningDate.isEqual(lastDate.plusDays(1))) {
                currentStreak = 0;
            }

            currentStreak++;
            lastDate = learningDate;

            System.out.println("Checking date: " + learningDate);
            System.out.println("Current streak: " + currentStreak + ", Max consecutive days: " + maxConsecutiveDays);

            maxConsecutiveDays = Math.max(maxConsecutiveDays, currentStreak);
        }

        System.out.println("Consecutive days calculated: " + maxConsecutiveDays);
        return maxConsecutiveDays;
    }
}
