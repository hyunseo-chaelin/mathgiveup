package hanium.smath.Game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResult {
    private String winnerId;
    private int winnerScore;
    private int loserScore;
    private String loserId;

    // 생성자 추가
    public GameResult(String winnerId, int winnerScore, int loserScore, String loserId) {
        this.winnerId = winnerId;
        this.winnerScore = winnerScore;
        this.loserScore = loserScore;
        this.loserId = loserId;
    }
}
