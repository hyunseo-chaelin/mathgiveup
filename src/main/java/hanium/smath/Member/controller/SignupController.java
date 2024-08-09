package hanium.smath.Member.controller;

import hanium.smath.Member.dto.SignupRequest;
import hanium.smath.Member.service.SignupService;
import hanium.smath.Member.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class SignupController {

    private final SignupService signupService;
    private final EmailService emailService;

    @Autowired
    public SignupController(SignupService signupService, EmailService emailService) {
        this.signupService = signupService;
        this.emailService = emailService;
    }

    // 이메일 인증 코드 발송
    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        if (signupService.checkEmailExists(email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        } else {
            signupService.saveEmail(email);
            emailService.sendVerificationCode(email, "registration");
            return ResponseEntity.ok("Verification email sent");
        }
    }

    // 이메일 인증 코드 확인
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam int code) {
        if (emailService.verifyEmailCode(email, code)) {
            signupService.markEmailAsVerified(email);
            return ResponseEntity.ok("Email verified");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> registerMember(@RequestBody SignupRequest request) {
        if (signupService.checkLoginIdExists(request.getLoginId())) {
            return ResponseEntity.badRequest().body("Login ID already exists");
        }

        if (!signupService.isEmailVerified(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is not verified");
        }

        signupService.registerMember(
                request.getEmail(),
                request.getName(),
                request.getLoginId(),
                request.getLoginPwd(),
                request.getNickname(),
                request.getGrade(),
                request.getBirthdate(),
                request.getPhoneNum()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body("Member registered successfully");
    }
}
