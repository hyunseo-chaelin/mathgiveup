package hanium.smath.Member.repository;

import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndBirthdate(String email, LocalDate birthdate);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByGoogleId(String googleId);
    Optional<Member> findByEmail(String email);  // 이메일로 사용자 조회
    // 카카오 ID로 회원을 찾는 메서드 추가
    Optional<Member> findByKakaoId(String kakaoId);  // 카카오 ID로 사용자 조회
}
