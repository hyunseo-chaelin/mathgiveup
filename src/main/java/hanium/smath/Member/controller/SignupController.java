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

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService;
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

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam int code) {
        boolean isVerified = signupService.verifyEmailCode(email, code);

        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<String> registerMember(@RequestBody SignupRequest signupRequest) {
        if (signupService.checkLoginIdExists(signupRequest.getLoginId())) {
            return ResponseEntity.badRequest().body("Login ID already exists");
        }

        if (!signupService.verifyEmailCode(signupRequest.getEmail(), signupRequest.getVerificationCode())) {
            return ResponseEntity.badRequest().body("Email verification failed");
        }

        signupService.registerMember(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Member registered successfully");
    }
}
