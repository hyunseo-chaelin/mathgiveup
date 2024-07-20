package hanium.smath.Member.repository;

import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}
