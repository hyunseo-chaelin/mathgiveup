package hanium.smath.Member.controller;

import hanium.smath.Member.dto.SignupRequest;
import hanium.smath.Member.service.SignupService;
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

    @PostMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        if (signupService.checkEmailExists(email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        } else {
            return ResponseEntity.ok("Email is available");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerMember(@RequestBody SignupRequest request) {

        if (signupService.checkLoginIdExists(request.getLoginId())) {
            return ResponseEntity.badRequest().body("Login ID already exists");
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
