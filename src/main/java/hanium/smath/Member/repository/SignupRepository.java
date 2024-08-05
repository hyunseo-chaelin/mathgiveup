package hanium.smath.Member.repository;

import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByLoginId(String loginId);
}