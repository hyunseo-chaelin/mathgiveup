package hanium.smath.Member.repository;

import hanium.smath.Member.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//이메일 인증 코드를 데이터베이스에서 관리하는 리포지토리
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByMember_LoginIdAndVerifiedEmailFalse(String loginId);
    Optional<EmailVerification> findTopByMember_LoginIdAndVerifiedEmailFalseOrderByCreateTimeDesc(String loginId);
}
