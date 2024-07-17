package hanium.smath.Member.controller;

import hanium.smath.Member.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmailService emailService;

    @Autowired
    public AuthController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/sendVerificationCode")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        try {
            emailService.sendVerificationEmail(email);
            return ResponseEntity.ok("Verification code sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");

        if (emailService.verifyCode(email, code)) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code");
        }
    }

//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> payload) {
//        String email = payload.get("email");
//        String name = payload.get("name");
//        String password = payload.get("password");
//        String nickname = payload.get("nickname");
//
//        try {
//            // Firebase Auth에 사용자 생성
//            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
//                    .setEmail(email)
//                    .setPassword(password)
//                    .setDisplayName(nickname);
//
//            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
//
//            // 추가적인 사용자 정보 저장 로직 필요 시 구현
//            return ResponseEntity.ok("User registered successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }
}
