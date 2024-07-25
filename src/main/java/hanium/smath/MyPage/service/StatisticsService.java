package hanium.smath.MyPage.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import hanium.smath.MyPage.dto.DailyStatisticsResponse;
import hanium.smath.MyPage.entity.GameSession;
import hanium.smath.MyPage.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class StatisticsService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private StatisticsRepository statisticsRepository;

    public CompletableFuture<DailyStatisticsResponse> getDailyStatistics(String idMember, LocalDate date) {
        System.out.println("Fetching daily statistics for user: " + idMember + " on date: " + date);

        DocumentReference memberRef = firestore.collection("Members").document(idMember);
        System.out.println("Created DocumentReference for member: " + idMember);

        CompletableFuture<List<QueryDocumentSnapshot>> future = statisticsRepository.findGameSessions(idMember);

        return future.thenApply(documents -> {
            System.out.println("Found " + documents.size() + " game sessions for member: " + idMember);

            DailyStatisticsResponse response = new DailyStatisticsResponse();
            int totalProblems = 0;
            int totalCorrect = 0;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (QueryDocumentSnapshot document : documents) {
                GameSession session = document.toObject(GameSession.class);
                LocalDate sessionDate = LocalDate.parse(session.getSessionDate(), formatter);

                if (sessionDate.equals(date)) {
                    System.out.println("Processing game session: " + session.getIdSession());
                    response.addGameStatistics(session.getGameType(), session.getProblemsSolved(), session.getCorrectAnswers());

                    totalProblems += session.getProblemsSolved();
                    totalCorrect += session.getCorrectAnswers();
                }
            }

            response.setTotalProblemsSolved(totalProblems);
            response.setAccuracyRate(totalProblems > 0 ? (double) totalCorrect / totalProblems * 100 : 0);  // 백분율
            System.out.println("Total problems solved: " + totalProblems + ", Accuracy rate: " + response.getAccuracyRate());

            return response; // CompletableFuture의 결과로 반환
        }).exceptionally(e -> {
            System.err.println("Error fetching game sessions: " + e.getMessage());
            return null; // 예외 발생 시 null 반환
        });
    }
}
