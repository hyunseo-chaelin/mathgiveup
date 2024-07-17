//package hanium.smath.Member.service;
//
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class EmailService {
//
//    private final JavaMailSender javaMailSender;
//    private final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();
//
//    public EmailService(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//    public void sendVerificationEmail(String email) { //인증 코드 생성하고 이메일로 전송
//        String code = generateVerificationCode();
//        verificationCodes.put(email, code);
//
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//
//        try {
//            helper.setText("Your verification code is: " + code, true);
//            helper.setTo(email);
//            helper.setSubject("Email Verification");
//            helper.setFrom("no-reply@smath.com");
//            javaMailSender.send(mimeMessage);
//        } catch (MessagingException e) {
//            throw new IllegalStateException("Failed to send email", e);
//        }
//    }
//
//    public boolean verifyCode(String email, String code) { // 사용자가 입력한 인증 코드 확인
//        return code.equals(verificationCodes.get(email));
//    }
//
//    private String generateVerificationCode() {
//        Random random = new Random();
//        int code = 100000 + random.nextInt(900000);
//        return String.valueOf(code);
//    }
//}
