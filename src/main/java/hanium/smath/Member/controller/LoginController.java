package hanium.smath.Member.controller;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.service.GoogleLoginService;
import hanium.smath.Member.service.LoginService;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.TimeZone;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/members")
public class LoginController {

    private LoginService loginService;
    private final GoogleLoginService googleLoginService;

    @Autowired
    public LoginController(LoginService loginService, GoogleLoginService googleLoginService) {
        this.loginService = loginService; // controller가 생성될 때 service 주입하기
        System.out.println("MemberController instantiated with MemberService");
        this.googleLoginService = googleLoginService;
        System.out.println("MemberController instantiated with googleLoginService");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Member member) {
        System.out.println("RequestBody received: " + member);

        if(member.getLogin_id() == null || member.getLogin_id().isEmpty()) {
            return ResponseEntity.badRequest().body("Login_id is empty.");
        }

        if(member.getLogin_pwd() == null || member.getLogin_pwd().isEmpty()) {
            return ResponseEntity.badRequest().body("Login_pwd is empty.");
        }

        try {
            System.out.println("Login request received for ID: " + member.getLogin_id());

            Member infoMember = loginService.getMemberById(member.getLogin_id());

            if(infoMember != null) {
                if (infoMember.getLogin_pwd().equals(member.getLogin_pwd())) {
                    System.out.println("Login successful for ID: " + member.getLogin_id());
                    return ResponseEntity.ok("Login successful ! Nickname : " + infoMember.getNickname());
                } else {
                    System.out.println("Login failed for Password: " + member.getLogin_id());
                    return ResponseEntity.ok("Invalid password");
                }
            } else {
                System.out.println("Login failed for ID: " + member.getLogin_id());
                return ResponseEntity.status(401).body("Login faild for ID");
            }
        } catch (ExecutionException | InterruptedException e) { // 비동기 및 스레드 오류
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during login: " + e.getMessage());
        } catch (IllegalArgumentException e) { // 메서드가 잘못된 인수로 인한 오류
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<String> googleLogin(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");

        try {
            System.out.println("ID Token received: " + idToken);

            Member member = googleLoginService.processGoogleLogin(idToken);

            if (member != null) {
                return ResponseEntity.ok("Login successful! Nickname: " + member.getNickname());
            } else {
                return ResponseEntity.status(401).body("Login failed for Google ID");
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during login: " + e.getMessage());
        }
    }

    @GetMapping("/find/loginId")
    public ResponseEntity<String> findLoginId(@RequestParam String email, String birthDate) {
        System.out.println("Received request to find login ID");

        try {
            String loginId = loginService.findLoginId(email,birthDate);
            System.out.println("Found ID: " + loginId);
            return ResponseEntity.ok("Found ID! ID: " + loginId);
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            System.err.println("Error finding login ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset/password")
    public RedirectView resetPassword(@RequestParam String loginId) {
        try {
            String password = loginService.findLoginPwd(loginId);
            return new RedirectView("/api/members/password-reset-page?loginId=" + loginId); // 비밀번호 재설정 페이지로 이동
        } catch (IllegalArgumentException e) {
            return new RedirectView("/api/members/find/loginId"); // ID 찾기 페이지로 리다이렉트
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return new RedirectView("/error"); // 서버 오류 페이지로 리다이렉트
        }
    }
}
