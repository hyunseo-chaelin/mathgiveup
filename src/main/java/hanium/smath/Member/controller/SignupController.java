package hanium.smath.Member.controller;

import hanium.smath.Member.entity.Member;
import java.util.concurrent.*;

import hanium.smath.Member.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/members")
public class SignupController {

    private SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService; // controller가 생성될 때 service 주입하기
        System.out.println("MemberController instantiated with MemberService");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody Member member) {
        System.out.println("RequestBody received: " + member);
        try {
            String memberId = signupService.createMember(member);
            return ResponseEntity.ok("Member created with ID: " + memberId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return ResponseEntity.status(500).body("Error creating member: " + e.getMessage());
        }
    }
}
