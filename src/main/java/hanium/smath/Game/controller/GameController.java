package hanium.smath.Game.controller;

import hanium.smath.Game.dto.GameResult;
import hanium.smath.Game.dto.GameSelection;
import hanium.smath.Game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/select")
    @SendTo("/topic/game")
    public GameResult handleSelection(GameSelection selection) {
        // gameService를 사용하여 게임 로직을 처리하고 결과 반환
        return gameService.processSelection(selection);
    }
}
