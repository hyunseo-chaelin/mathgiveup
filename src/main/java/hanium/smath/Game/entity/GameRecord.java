package hanium.smath.Game.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gamerecord")
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "winner_id", nullable = false)
    private Player winner;

    @ManyToOne
    @JoinColumn(name = "loser_id", nullable = false)
    private Player loser;

    private LocalDateTime gameTime; // 게임이 발생한 날짜와 시간

}
