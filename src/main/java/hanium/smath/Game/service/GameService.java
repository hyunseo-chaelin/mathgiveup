package hanium.smath.Game.service;

import hanium.smath.Game.dto.GameResult;
import hanium.smath.Game.dto.GameSelection;
import hanium.smath.Game.entity.GameRecord;
import hanium.smath.Game.entity.Player;
import hanium.smath.Game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, Long> playerSelections = new ConcurrentHashMap<>();
    private final PlayerRepository playerRepository;

    @Autowired
    public GameService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public GameResult processSelection(GameSelection selection) {
        String playerId = selection.getPlayerId();
        long selectionTime = System.currentTimeMillis();

        // 현재 선택한 사용자의 선택 시간을 기록
        playerSelections.put(playerId, selectionTime);

        // 두 플레이어의 선택 시간을 비교하여 결과 결정
        if (playerSelections.size() == 2) {
            return determineWinner();
        } else {
            return null; // 두 플레이어가 모두 선택할 때까지 대기
        }
    }

    private GameResult determineWinner() {
        List<Map.Entry<String, Long>> entries = new ArrayList<>(playerSelections.entrySet());

        // 가장 빠른 선택을 한 사용자를 찾음
        entries.sort(Map.Entry.comparingByValue());

        String winnerId = entries.get(0).getKey();
        String loserId = entries.get(1).getKey(); // 패배자의 ID

        // 승자와 패자를 Player 엔티티에서 가져옴
        Player winner = playerRepository.findByMember_LoginId(winnerId)
                .orElseThrow(() -> new RuntimeException("Winner not found"));
        Player loser = playerRepository.findByMember_LoginId(loserId)
                .orElseThrow(() -> new RuntimeException("Loser not found"));

        // 승자에게 승리 추가, 패자에게 패배 추가
        winner.addWin();
        loser.addLoss();

        // 변경된 승/패 정보를 저장
        playerRepository.save(winner);
        playerRepository.save(loser);

        int winnerScore = 1; // 승자에게 부여할 점수
        int loserScore = 0;  // 패배자에게 부여할 점수

        // 승자와 패배자의 점수를 포함한 결과 객체를 반환
        GameResult result = new GameResult(winnerId, winnerScore, loserScore, loserId);
        playerSelections.clear(); // 상태 초기화
        return result;
    }

}