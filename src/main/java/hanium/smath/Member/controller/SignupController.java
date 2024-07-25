package hanium.smath.Member.controller;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/signup")
public class SignupController {
    private final SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/checkEmail")
    public ResponseEntity<String> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = signupService.checkEmailExists(email);
            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            } else {
                return ResponseEntity.ok("Email does not exist");
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking email");
        }
    }

    @PostMapping("/checkLoginId")
    public ResponseEntity<String> checkLoginIdExists(@RequestParam String loginId) {
        System.out.println("Checking if loginId exists: " + loginId);
        try {
            boolean exists = signupService.checkLoginIdExists(loginId);
            if (exists) {
                System.out.println("ID already exists: " + loginId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("ID already exists");
            } else {
                System.out.println("ID does not exist: " + loginId);
                return ResponseEntity.ok("ID does not exist");
            }
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error checking ID: " + loginId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking ID");
        }
    }

    @PostMapping("/register")
    public void registerMember(@RequestBody Member member) {
        System.out.println("Registering member: " + member);
        signupService.registerMember(member);
    }

    @PostMapping("/sendCode")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        System.out.println("Sending verification code to email: " + email);
        try {
            signupService.sendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent successfully");
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error sending verification code to email: " + email);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending verification code");
        }
    }

    @PostMapping("/verifyCode")
    public boolean verifyEmailCode(@RequestParam String email, @RequestParam String code) throws ExecutionException, InterruptedException {
        System.out.println("Verifying email code for email: " + email + ", code: " + code);
        return signupService.verifyEmailCode(email, code);
    }
}
