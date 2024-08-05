package hanium.smath.Member.service;

import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.repository.LoginRepository;
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
import java.util.Random;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final LoginRepository loginRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, EmailVerificationRepository emailVerificationRepository, LoginRepository loginRepository) {
        this.javaMailSender = javaMailSender;
        this.emailVerificationRepository = emailVerificationRepository;
        this.loginRepository = loginRepository;
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

    private int generateVerificationCode() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // 4자리 랜덤 숫자 생성
    }


    // 인증 코드를 생성하여 지정된 이메일로 전송하고, 데이터베이스에 저장
    public void sendVerificationCode(String loginId, String email) {
        System.out.println("EmailService: Sending verification code to email: " + email);
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
        return emailVerificationRepository.findByMember_LoginIdAndVerifiedEmailFalse(loginId)
                .map(emailVerification -> {
                    System.out.println("EmailService: Found verification code for loginId: " + loginId + ", storedCode: " + emailVerification.getVerificationCode());
                    boolean verified = code == emailVerification.getVerificationCode();
                    if (verified) {
                        emailVerification.setVerifiedEmail(true);
                        emailVerificationRepository.save(emailVerification);
                        System.out.println("EmailService: Email code verified and marked as used for loginId: " + loginId);
                    } else {
                        System.out.println("EmailService: Provided code does not match stored code for loginId: " + loginId);
                    }
                    System.out.println("EmailService: Email code verification result: " + verified);
                    return verified;
                })
                .orElseGet(() -> {
                    System.out.println("EmailService: No verification code found for loginId: " + loginId + " or email already verified.");
                    return false;
                });
    }

//    private void markEmailAsVerified(String loginId) {
//        String query = "UPDATE EmailVerification SET verifiedEmail = 1 WHERE login_id = ?";
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, loginId);
//            statement.executeUpdate();
//            System.out.println("EmailService: Email marked as verified for loginId: " + loginId);
//        } catch (SQLException e) {
//            System.err.println("Error marking email as verified: " + e.getMessage());
//            throw new RuntimeException("Failed to mark email as verified", e);
//        }
//    }

}
