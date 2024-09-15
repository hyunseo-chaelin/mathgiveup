package hanium.smath.Game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSelection {
    private String playerId;
    private String choice;
    private boolean isCorrect; // 플레이어의 선택이 정답인지 여부를 나타내는 필드 추가
}
