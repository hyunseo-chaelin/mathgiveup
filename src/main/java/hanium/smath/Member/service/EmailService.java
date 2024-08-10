package hanium.smath.Member.service;

import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.Member.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final LoginRepository loginRepository;
    private final SignupRepository signupRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, EmailVerificationRepository emailVerificationRepository, LoginRepository loginRepository, SignupRepository signupRepository) {
        this.javaMailSender = javaMailSender;
        this.emailVerificationRepository = emailVerificationRepository;
        this.loginRepository = loginRepository;
        this.signupRepository = signupRepository;
    }

    //(Signup): 이메일만 받고 인증 코드를 전송
    public void sendVerificationCodeToEmailOnly(String email) {
        System.out.println("EmailService: Sending verification code to email: " + email);
        int code = generateVerificationCode();
        sendEmail(email, "Verification Code", "Your verification code is: " + code);
        saveVerificationCodeByEmail(email, code);
        System.out.println("EmailService: Verification code sent to email only: " + code);
    }

    // 이메일만으로 인증 코드를 저장
    private void saveVerificationCodeByEmail(String email, int code) {
        // 이메일로 이전에 저장된 인증 코드가 있으면 해당 항목을 무효화합니다.
        Optional<EmailVerification> existingVerification = emailVerificationRepository.findTopByEmailAndVerifiedEmailFalseOrderByCreateTimeDesc(email);

        if (existingVerification.isPresent()) {
            EmailVerification oldVerification = existingVerification.get();
            oldVerification.setVerifiedEmail(true); // 이전 인증 코드를 무효화
            emailVerificationRepository.save(oldVerification);
        }

        // 새로운 인증 코드 생성 및 저장
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail(email); // 이메일 설정
        emailVerification.setVerificationCode(code);
        emailVerification.setVerifiedEmail(false);
        emailVerification.setCreateTime(LocalDateTime.now());
        emailVerificationRepository.save(emailVerification);
        System.out.println("EmailService: Verification code saved for email: " + email);
    }


    // 이메일과 인증 코드를 검증하는 메서드
    public boolean verifyEmailCodeByEmail(String email, int code) {
        System.out.println("EmailService: Verifying email code for email: " + email + ", code: " + code);
        Optional<EmailVerification> optionalEmailVerification = emailVerificationRepository.findTopByEmailAndVerifiedEmailFalseOrderByCreateTimeDesc(email);

        if (optionalEmailVerification.isPresent()) {
            EmailVerification emailVerification = optionalEmailVerification.get();
            System.out.println("EmailService: Found verification code for email: " + email + ", storedCode: " + emailVerification.getVerificationCode());
            boolean verified = code == emailVerification.getVerificationCode();

            if (verified) {
                // 인증 성공 시, 인증 상태를 true로 변경
                emailVerification.setVerifiedEmail(true);
                emailVerificationRepository.save(emailVerification);
                System.out.println("EmailService: Email code verification successful for email: " + email);
            } else {
                System.out.println("EmailService: Email code verification failed for email: " + email);
            }

            return verified;
        } else {
            System.out.println("EmailService: No verification code found for email: " + email + " or email already verified.");
            return false;
        }
    }



    public void sendEmail(String to, String subject, String text) {
        System.out.println("EmailService: Preparing to send email to: " + to);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(message);
            System.out.println("EmailService: Email sent successfully to: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("EmailService: Failed to send email to: " + to);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public int generateVerificationCode() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // 4자리 랜덤 숫자 생성
    }

    // 이전 인증코드 무효화
    private void invalidateOldVerificationCodes(String loginId) {
        System.out.println("EmailService: Invalidating old verification codes for loginId: " + loginId);
        emailVerificationRepository.findByMember_LoginIdAndVerifiedEmailFalse(loginId)
                .ifPresent(emailVerification -> {
                    emailVerification.setVerifiedEmail(true);
                    emailVerificationRepository.save(emailVerification);
                    System.out.println("EmailService: Old verification code invalidated for loginId: " + loginId);
                });
    }

    // 인증 코드를 생성하여 지정된 이메일로 전송하고, 데이터베이스에 저장
    public void sendVerificationCode(String loginId, String email) {
        System.out.println("EmailService: Sending verification code to email: " + email);
        invalidateOldVerificationCodes(loginId); // 이전 인증 코드 무효화
        int code = generateVerificationCode();
        sendEmail(email, "Verification Code", "Your verification code is: " + code);
        saveVerificationCode(loginId, code);
        System.out.println("EmailService: Verification code sent: " + code);
    }

    // 생성된 인증 코드를 데이터베이스에 저장
    private void saveVerificationCode(String loginId, int code) {
        System.out.println("EmailService: Saving verification code for loginId: " + loginId + ", code: " + code);
        Member member = loginRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login_id: " + loginId));
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setMember(member);
        emailVerification.setVerificationCode(code);
        emailVerification.setVerifiedEmail(false);
        emailVerification.setCreateTime(LocalDateTime.now());
        emailVerificationRepository.save(emailVerification);
        System.out.println("EmailService: Verification code saved");
    }

    //사용자가 입력한 인증 코드가 유효한지 검증
    public boolean verifyEmailCode(String loginId, int code) {
        System.out.println("EmailService: Verifying email code for loginId: " + loginId + ", code: " + code);
        Optional<EmailVerification> optionalEmailVerification = emailVerificationRepository.findTopByMember_LoginIdAndVerifiedEmailFalseOrderByCreateTimeDesc(loginId);

        if (optionalEmailVerification.isPresent()) {
            EmailVerification emailVerification = optionalEmailVerification.get();
            System.out.println("EmailService: Found verification code for loginId: " + loginId + ", storedCode: " + emailVerification.getVerificationCode());
            boolean verified = code == emailVerification.getVerificationCode();
            System.out.println("EmailService: Email code verification result for loginId: " + loginId + " is " + verified);
            return verified;
        } else {
            System.out.println("EmailService: No verification code found for loginId: " + loginId + " or email already verified.");
            return false;
        }
    }

    public void invalidateVerificationCode(String loginId, int code) {
        System.out.println("EmailService: Invalidating verification code for loginId: " + loginId + ", code: " + code);
        Optional<EmailVerification> optionalEmailVerification = emailVerificationRepository.findTopByMember_LoginIdAndVerifiedEmailFalseOrderByCreateTimeDesc(loginId);

        if (optionalEmailVerification.isPresent()) {
            EmailVerification emailVerification = optionalEmailVerification.get();
            if (code == emailVerification.getVerificationCode()) {
                emailVerification.setVerifiedEmail(true);
                emailVerificationRepository.save(emailVerification);
                System.out.println("EmailService: Verification code invalidated for loginId: " + loginId + ", code: " + code);
            }
        }
    }
}
