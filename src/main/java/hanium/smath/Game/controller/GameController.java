package hanium.smath.Game.controller;

import hanium.smath.Game.dto.GameResult;
import hanium.smath.Game.dto.GameSelection;
import hanium.smath.Game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

@Controller
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/select")
    @SendTo("/topic/game")
    public GameResult handleSelection(GameSelection selection) {
        // gameService를 사용하여 게임 로직을 처리하고 결과 반환
        return gameService.processSelection(selection);
    }

    @GetMapping("/final-result")
    public ResponseEntity<GameResult> getFinalResult(@RequestParam String player1Id, @RequestParam String player2Id) {
        // 게임이 종료된 후 최종 결과를 반환
        GameResult finalResult = gameService.getFinalResult(player1Id, player2Id);
        return ResponseEntity.ok(finalResult);
    }
}
