package hanium.smath.Member.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {
    private final Map<String, String> emailVerificationCodes = new HashMap<>();

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 인증 코드 생성
        return String.valueOf(code);
    }

    public void saveVerificationCode(String email, String code) {
        emailVerificationCodes.put(email, code);
    }

    public void sendVerificationCode(String email, String code) {
        // 실제 이메일 전송 로직 구현
        System.out.println("Sending verification code " + code + " to email " + email);
    }

    public boolean verifyCode(String email, String code) {
        String savedCode = emailVerificationCodes.get(email);
        return savedCode != null && savedCode.equals(code);
    }
}
