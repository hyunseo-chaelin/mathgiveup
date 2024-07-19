package hanium.smath.Member.controller;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signup")
public class SignupController {
    private final SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/check-email")
    public String checkEmail(@RequestParam String email) {
        if (signupService.checkEmailExists(email)) {
            return "Email already exists";
        } else {
            signupService.sendVerificationCode(email);
            return "Verification code sent";
        }
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, @RequestParam String code) {
        if (signupService.verifyEmailCode(email, code)) {
            return "Email verified";
        } else {
            return "Invalid verification code";
        }
    }

    @PostMapping("/register")
    public String registerMember(@RequestBody Member member) {
        if (signupService.checkEmailExists(member.getEmail())) {
            return "Email already exists";
        } else {
            signupService.registerMember(member);
            return "Member registered successfully";
        }
    }
}
