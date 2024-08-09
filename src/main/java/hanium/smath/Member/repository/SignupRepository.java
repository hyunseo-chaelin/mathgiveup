package hanium.smath.Member.repository;

import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignupRepository extends JpaRepository<Member, Long> {
    // 로그인 아이디로 멤버 조회
    Optional<Member> findByLoginId(String loginId);

    // 이메일로 멤버 조회
    Optional<Member> findByEmail(String email);

    // 로그인 아이디가 이미 존재하는지 확인
    boolean existsByLoginId(String loginId);

    // 이메일이 이미 존재하는지 확인
    boolean existsByEmail(String email);
}
