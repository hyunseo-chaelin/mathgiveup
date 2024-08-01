//package hanium.smath.Member.controller;
//
//import hanium.smath.Member.service.SignupService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.concurrent.ExecutionException;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//    private final SignupService signupService;
//
//    @Autowired
//    public AuthController(SignupService signupService) {
//        this.signupService = signupService;
//    }
//
//    @PostMapping("/verify-email")
//    public String verifyEmail(@RequestParam String email, @RequestParam String code) throws ExecutionException, InterruptedException {
//        System.out.println("AuthController: Verifying email: " + email + " with code: " + code);
//        if (signupService.verifyEmailCode(email, code)) {
//            System.out.println("AuthController: Email verified successfully for: " + email);
//            return "Email verified";
//        } else {
//            System.out.println("AuthController: Invalid verification code for email: " + email);
//            return "Invalid verification code";
//        }
//    }
//}
