package hanium.smath.Member.service;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.SignupRepository;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

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
        Optional<Member> optionalMember = signupRepository.findByEmail(signupRequest.getEmail());
        Member member;

        if (optionalMember.isPresent()) {
            member = optionalMember.get();
        } else {
            throw new IllegalArgumentException("Email not verified or not found.");
        }

        member.setLoginId(signupRequest.getLoginId());
        member.setLoginPwd(signupRequest.getLoginPwd());
        member.setName(signupRequest.getName());
        member.setNickname(signupRequest.getNickname());
        member.setBirthdate(signupRequest.getBirthdate());
        member.setGrade(signupRequest.getGrade());
        member.setPhoneNum(signupRequest.getPhoneNum());
        member.setEmailVerified(true); // 이메일 인증 완료로 설정

        signupRepository.save(member);
    }
}
