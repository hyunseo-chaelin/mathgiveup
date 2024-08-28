package hanium.smath.Game.entity;

import hanium.smath.Member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "login_id", referencedColumnName = "login_id", nullable = false)
    private Member member;

    private int wins;        // 1:1 게임에서의 승리 수
    private int losses;      // 1:1 게임에서의 패배 수

    public void addWin() {
        this.wins += 1;
    }

    public void addLoss() {
        this.losses += 1;
    }
}
