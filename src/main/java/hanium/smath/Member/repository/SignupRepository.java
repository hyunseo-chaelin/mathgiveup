package hanium.smath.Member.repository;

import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupRepository extends JpaRepository<Member, Long> {
    // 로그인 아이디가 이미 존재하는지 확인
    boolean existsByLoginId(String loginId);

    // 이메일이 이미 존재하는지 확인
    boolean existsByEmail(String email);
}