package hanium.smath.MyPage.service;

import com.google.cloud.firestore.Firestore;
import hanium.smath.MyPage.dto.LearningRecordResponse;
import hanium.smath.MyPage.entity.LearningRecord;
import hanium.smath.MyPage.repository.LearningRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.google.cloud.firestore.DocumentReference;

import static hanium.smath.MyPage.repository.LearningRecordRepository.formatter;

@Service
public class LearningService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private LearningRecordRepository repository;

    public Mono<LearningRecordResponse> getMonthlyLearningRecords(String idMember, String yearMonth) {
        try {
            System.out.println("Fetching monthly learning records for memberId: " + idMember + ", yearMonth: " + yearMonth);
            DocumentReference memberRef = firestore.collection("Members").document(idMember);

            YearMonth ym = YearMonth.parse(yearMonth);
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();

            System.out.println("Fetching monthly learning records for memberId: " + idMember + ", yearMonth: " + yearMonth);
            System.out.println("Calculated startDate: " + startDate + ", endDate: " + endDate);


            List<LearningRecord> records = repository.findByMemberIdAndLearningDateBetween(memberRef, startDate, endDate);

            List<String> learningDays = records.stream()
                    .filter(record -> {
                        LocalDate learningDate = LocalDate.parse(record.getLearningDate(), LearningRecordRepository.formatter);
                        return !learningDate.isBefore(startDate) && !learningDate.isAfter(endDate);
                    })
                    .map(LearningRecord::getLearningDate)
                    .collect(Collectors.toList());

            System.out.println("Learning days retrieved: " + learningDays);

            // 주석 추가 시작
            long consecutiveDays = calculateConsecutiveLearningDays(records, startDate, endDate);
            // 주석 추가 끝
            System.out.println("Consecutive learning days: " + consecutiveDays);


            LearningRecordResponse response = new LearningRecordResponse(idMember, yearMonth, learningDays, consecutiveDays);
            return Mono.just(response);
        } catch (Exception e) {
            System.err.println("Error fetching learning records: " + e.getMessage());
            return Mono.error(e);
        }
    }

    private long calculateConsecutiveLearningDays(List<LearningRecord> records, LocalDate startDate, LocalDate endDate) {
        records.sort((record1, record2) -> {
            LocalDate date1 = LocalDate.parse(record1.getLearningDate(), LearningRecordRepository.formatter);
            LocalDate date2 = LocalDate.parse(record2.getLearningDate(), LearningRecordRepository.formatter);
            return date1.compareTo(date2);
        });

        System.out.println("Sorted records by learningDate: " + records);

        long maxConsecutiveDays = 0;
        long currentStreak = 0;

        LocalDate lastDate = null;

        for (LearningRecord record : records) {
            LocalDate learningDate = LocalDate.parse(record.getLearningDate(), formatter);

            if (lastDate != null && !learningDate.isEqual(lastDate.plusDays(1))) {
                currentStreak = 0;
            }

            currentStreak++;
            lastDate = learningDate;

            System.out.println("Checking date: " + learningDate);
            System.out.println("Current streak: " + currentStreak + ", Max consecutive days: " + maxConsecutiveDays);

            if (learningDate.isAfter(endDate)) {
                break;
            }

            if (learningDate.isAfter(startDate.minusDays(1))) {
                maxConsecutiveDays = Math.max(maxConsecutiveDays, currentStreak);
            }
        }

        System.out.println("Consecutive days calculated: " + maxConsecutiveDays);
        return maxConsecutiveDays;
    }
}
