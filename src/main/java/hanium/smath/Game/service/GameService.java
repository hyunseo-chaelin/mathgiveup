package hanium.smath.Game.service;

import hanium.smath.Game.dto.GameResult;
import hanium.smath.Game.dto.GameSelection;
import hanium.smath.Game.entity.Player;
import hanium.smath.Game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {

    private final Map<String, Integer> playerScores = new ConcurrentHashMap<>(); // 각 플레이어의 점수를 추적
    private final Map<String, Boolean> playerLock = new ConcurrentHashMap<>();   // 플레이어의 클릭 잠금 상태 추적

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 스케줄러 추가


    public GameResult processSelection(GameSelection selection) {
        String playerId = selection.getPlayerId();

        // 플레이어가 현재 클릭 금지 상태라면 선택을 무시함
        if (playerLock.getOrDefault(playerId, false)) {
            return new GameResult(playerId, playerScores.getOrDefault(playerId, 0), 0, null); // 무시할 때는 점수 변화 없음
        }

        // 플레이어의 선택이 정답인 경우에만 점수를 부여
        if (selection.isCorrect()) {
            awardPointToPlayer(playerId);
        } else {
            // 틀린 경우 5초간 클릭 금지
            lockPlayer(playerId, 5); // 5초 동안 클릭 금지
        }

        // 현재까지의 점수를 포함한 결과 반환
        int currentScore = playerScores.getOrDefault(playerId, 0);
        return new GameResult(playerId, currentScore, 0, null); // 패배자는 null로 처리
    }

    private void awardPointToPlayer(String playerId) {
        // 플레이어에게 1점 추가
        playerScores.put(playerId, playerScores.getOrDefault(playerId, 0) + 1);
    }

    // 플레이어를 일정 시간 동안 잠금
    private void lockPlayer(String playerId, int seconds) {
        playerLock.put(playerId, true); // 플레이어 클릭 금지 설정

        // 5초 후 클릭 금지 해제
        scheduler.schedule(() -> playerLock.put(playerId, false), seconds, TimeUnit.SECONDS);
    }

    public GameResult getFinalResult(String player1Id, String player2Id) {
        int player1Score = playerScores.getOrDefault(player1Id, 0);
        int player2Score = playerScores.getOrDefault(player2Id, 0);

        String winnerId;
        String loserId;

        if (player1Score > player2Score) {
            winnerId = player1Id;
            loserId = player2Id;
        } else if (player2Score > player1Score) {
            winnerId = player2Id;
            loserId = player1Id;
        } else {
            winnerId = "Draw";
            loserId = "Draw";
        }

        return new GameResult(winnerId, Math.max(player1Score, player2Score), Math.min(player1Score, player2Score), loserId);
    }
}
