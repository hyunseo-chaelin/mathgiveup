package hanium.smath.Member.service;

import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.SignupRepository;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SignupService {

    private final SignupRepository signupRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;

    @Autowired
    public SignupService(SignupRepository signupRepository, EmailVerificationRepository emailVerificationRepository, EmailService emailService) {
        this.signupRepository = signupRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailService = emailService;
    }

    public boolean checkLoginIdExists(String loginId) {
        return signupRepository.existsByLoginId(loginId);
    }

    public boolean checkEmailExists(String email) {
        return signupRepository.existsByEmail(email);
    }

    public void sendVerificationCodeToEmail(String email) {
        emailService.sendVerificationCodeToEmailOnly(email);
    }

    public boolean verifyEmailCode(String email, int code) {
        return emailService.verifyEmailCodeByEmail(email, code);
    }

    public void registerMember(SignupRequest signupRequest) {
        EmailVerification emailVerification = emailVerificationRepository.findEmailVerificationByEmail(signupRequest.getEmail());

        if (emailVerification == null) {
            throw new IllegalArgumentException("Email verification record not found for email: " + signupRequest.getEmail());
        }

        LocalDate birthdate = LocalDate.parse(signupRequest.getBirthdate());

        Member member = Member.builder()
                .email(emailVerification.getEmail())
                .loginId(signupRequest.getLoginId())
                .loginPwd(signupRequest.getLoginPwd())
                .name(signupRequest.getName())
                .nickname(signupRequest.getNickname())
                .birthdate(birthdate)
                .grade(signupRequest.getGrade())
                .isEmailVerified(true) // 이메일 인증 완료로 설정
                .build();

        signupRepository.save(member);
    }

}
