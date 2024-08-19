package hanium.smath.Member.service;

import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.SignupRepository;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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
                .icon("icon1")
                .build();

        signupRepository.save(member);
    }

    @Transactional
    public void deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLoginId = authentication.getName(); // JWT에서 추출된 사용자 loginId

        // loginId로 사용자 조회
        Member member = signupRepository.findByLoginId(currentUserLoginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with loginId: " + currentUserLoginId));

        // 회원 삭제
        signupRepository.delete(member);

        // 해당 사용자의 이메일로 이메일 인증 정보 삭제
        emailVerificationRepository.deleteByEmail(member.getEmail());
    }

    @Transactional
    public void changeNickname(String newNickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLoginId = authentication.getName(); // JWT에서 추출된 사용자 loginId

        Member member = signupRepository.findByLoginId(currentUserLoginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with loginId: " + currentUserLoginId));

        // 닉네임 변경
        member.setNickname(newNickname);
        signupRepository.save(member);
    }

    @Transactional
    public void changeIcon(String newIcon) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLoginId = authentication.getName(); // JWT에서 추출된 사용자 loginId

        Member member = signupRepository.findByLoginId(currentUserLoginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with loginId: " + currentUserLoginId));

        // 아이콘 변경
        member.setIcon(newIcon);
        signupRepository.save(member);
    }
}
