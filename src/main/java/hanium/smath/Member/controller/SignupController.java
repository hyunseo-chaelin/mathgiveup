package hanium.smath.Member.controller;

import hanium.smath.Member.dto.SignupRequest;
import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.service.SignupService;
import hanium.smath.Member.service.EmailService;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class SignupController {

    private final SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService, EmailVerificationRepository emailVerificationRepository) {
        this.signupService = signupService;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    @PostMapping("/check-loginId")
    public ResponseEntity<String> checkLoginId(@RequestParam String loginId) {
        if (signupService.checkLoginIdExists(loginId)) {
            return ResponseEntity.badRequest().body("Login ID already exists");
        } else {
            return ResponseEntity.ok("Login ID is available");
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        if (signupService.checkEmailExists(email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        } else {
            signupService.sendVerificationCodeToEmail(email);
            return ResponseEntity.ok("Email is available. Verification code sent to email.");
        }
    }

    @PatchMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        int codeInt = Integer.parseInt(code);
        boolean isVerified = signupService.verifyEmailCode(email, codeInt);

        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
    }

    private final EmailVerificationRepository emailVerificationRepository;

    @PostMapping("/signup")
    public ResponseEntity<String> registerMember(@RequestBody SignupRequest signupRequest) {

        EmailVerification optionalEmailVerification = emailVerificationRepository.findEmailVerificationByEmail(signupRequest.getEmail());

        if (signupService.checkLoginIdExists(signupRequest.getLoginId())) {
            return ResponseEntity.badRequest().body("Login ID already exists");
        }

        if (!optionalEmailVerification.isVerifiedEmail()) {
            return ResponseEntity.badRequest().body("Email verification failed");
        }

        signupService.registerMember(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Member registered successfully");
    }
}
