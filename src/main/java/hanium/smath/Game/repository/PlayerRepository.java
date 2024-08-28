package hanium.smath.Game.repository;

import hanium.smath.Game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByMember_LoginId(String loginId);
}
