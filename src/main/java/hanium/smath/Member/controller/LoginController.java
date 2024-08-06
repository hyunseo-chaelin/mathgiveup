package hanium.smath.Member.controller;

//import hanium.smath.Member.dto.AuthResponse;
import hanium.smath.Member.dto.GoogleLoginRequest;
import hanium.smath.Member.dto.AuthResponse;
import hanium.smath.Member.dto.LoginRequest;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.security.JwtUtil;
import hanium.smath.Member.service.EmailService;
import hanium.smath.Member.service.GoogleLoginService;
import hanium.smath.Member.service.LoginService;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/members")
public class LoginController {

    private LoginService loginService;
    private final GoogleLoginService googleLoginService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Autowired
    public LoginController(LoginService loginService, GoogleLoginService googleLoginService, JwtUtil jwtUtil, EmailService emailService) {
        this.loginService = loginService; // controller가 생성될 때 service 주입하기
        System.out.println("MemberController instantiated with MemberService");
        this.googleLoginService = googleLoginService;
        System.out.println("MemberController instantiated with googleLoginService");
        this.jwtUtil = jwtUtil;
        System.out.println("MemberController instantiated with jwtUtil");
        this.emailService = emailService;
        System.out.println("MemberController instantiated with email");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("RequestBody received: " + loginRequest);

        if(loginRequest.getLogin_id() == null || loginRequest.getLogin_id().isEmpty()) {
            System.out.println("Login_id is empty.");
            return ResponseEntity.badRequest().body(new AuthResponse("Login_id is empty.", null, null));
        }

        if(loginRequest.getLogin_pwd() == null || loginRequest.getLogin_pwd().isEmpty()) {
            System.out.println("Login_pwd is empty.");
            return ResponseEntity.badRequest().body(new AuthResponse("Login_pwd is empty.", null, null));
        }

            try {
                System.out.println("Login request received for ID: " + loginRequest.getLogin_id());
                Member member= loginService.getMemberById(loginRequest.getLogin_id());

                if (member != null) {
                    System.out.println("Member found : " + loginRequest.getLogin_id());
                    if (member.getLoginPwd().equals(loginRequest.getLogin_pwd())) {
                        String token;
                        if (loginRequest.isAutoLogin()) {
                            System.out.println("Generating token with extended expiry for ID: " + loginRequest.getLogin_id());
                            token = jwtUtil.generateTokenWithExtendedExpiry(member.getLoginId());
                        } else {
                            System.out.println("Generating token for ID: " + loginRequest.getLogin_id());
                            token = jwtUtil.generateToken(member.getLoginId());
                        }
                        AuthResponse response = new AuthResponse("Login successful", member.getNickname(), token);

                        System.out.println("Login successful for ID: " + loginRequest.getLogin_id());
                        return ResponseEntity.ok(response);
                    } else {
                        System.out.println("Invalid login_pwd for ID: " + loginRequest.getLogin_id());
                        return ResponseEntity.status(401).body(new AuthResponse("Invalid login_pwd.", null, null));
                    }
                } else {
                    System.out.println("Invalid login_id: " + loginRequest.getLogin_id());
                    return ResponseEntity.status(401).body(new AuthResponse("Invalid login_id.", null, null));
                }
            } catch (Exception e) {
                System.err.println("Error during login: " + e.getMessage());
                return ResponseEntity.status(500).body(new AuthResponse("Error during login: " + e.getMessage(), null, null));
            }

//            if(infoMember != null) {
//                if (infoMember.getLogin_pwd().equals(member.getLogin_pwd())) {
//                    System.out.println("Login successful for ID: " + member.getLogin_id());
//                    return ResponseEntity.ok("Login successful ! Nickname : " + infoMember.getNickname());
//                } else {
//                    System.out.println("Login failed for Password: " + member.getLogin_id());
//                    return ResponseEntity.ok("Invalid password");
//                }
//            } else {
//                System.out.println("Login failed for ID: " + member.getLogin_id());
//                return ResponseEntity.status(401).body("Login faild for ID");
//            }
//        } catch (ExecutionException | InterruptedException e) { // 비동기 및 스레드 오류
//            System.err.println("Error during login: " + e.getMessage());
//            return ResponseEntity.status(500).body("Error during login: " + e.getMessage());
//        } catch (IllegalArgumentException e) { // 메서드가 잘못된 인수로 인한 오류
//            System.err.println("Error: " + e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (TimeoutException e) {
//            throw new RuntimeException(e);
//        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<String> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            System.out.println("ID Token received: " + request.getIdToken());

            Member member = googleLoginService.processGoogleLogin(request);

            if (member != null) {
                System.out.println("Member found : " + member.getNickname());
                return ResponseEntity.ok("Login successful! Nickname: " + member.getNickname());
            } else {
                System.out.println("Failed to process google login request");
                return ResponseEntity.status(401).body("Login failed for Google ID");
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during login: " + e.getMessage());
        }
    }

    @GetMapping("/find/loginId")
    public ResponseEntity<String> findLoginId(@RequestParam String email, @RequestParam String birthdate) {
        System.out.println("Received request to find login ID");

        try {
            LocalDate birthDateLocal = LocalDate.parse(birthdate);
            String loginId = loginService.findLoginId(email, birthDateLocal);
            System.out.println("Found ID: " + loginId);
            return ResponseEntity.ok("Found ID! ID: " + loginId);
        } catch (RuntimeException e) {
            System.err.println("Error finding login ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 멤버가 있으면 이메일 보내기
    @PatchMapping("/reset/password/initiate")
    public ResponseEntity initiatePasswordReset(@RequestParam String login_id) {
        System.out.println("LoginController: Initiating password reset for loginId: " + login_id);
        Member member = loginService.getMemberById(login_id);
        if (member != null) {
            emailService.sendVerificationCode(member.getLoginId(), member.getEmail());
            return ResponseEntity.ok("Verification email sent.");
        } else {
            System.out.println("LoginController: User not found for loginId: " + login_id);
            return ResponseEntity.status(404).body("User not found.");
        }
    }

    // 입력한 인증 코드를 검증
    @PatchMapping("/reset/password/verify")
    public ResponseEntity verifyCode(@RequestParam String login_id, @RequestParam int code) {
        System.out.println("LoginController: Verifying code for loginId: " + login_id + ", code: " + code);
        boolean codeValid = emailService.verifyEmailCode(login_id, code);
        if (codeValid) {
            return ResponseEntity.ok("Verification code valid.");
        } else {
            System.out.println("LoginController: Invalid verification code for loginId: " + login_id);
            return ResponseEntity.status(400).body("Invalid verification code.");
        }
    }

    // 인증 코드를 검증한 후 비밀번호를 변경
    @PatchMapping("/reset/password/change")
    public ResponseEntity changePassword(@RequestParam String login_id, @RequestParam String new_password, @RequestParam int code) {
        System.out.println("LoginController: Changing password for loginId: " + login_id + ", code: " + code);
        // 인증 코드 검증
        boolean codeValid = emailService.verifyEmailCode(login_id, code);
        if (!codeValid) {
            System.out.println("LoginController: Invalid verification code for loginId: " + login_id);
            return ResponseEntity.status(400).body("Invalid verification code.");
        }

        // 비밀번호 변경
        boolean passwordChanged = loginService.changeUserPassword(login_id, new_password);
        if (passwordChanged) {
            // 인증 코드 무효화
            emailService.invalidateVerificationCode(login_id, code);
            System.out.println("LoginController: Password changed successfully for loginId: " + login_id);
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            System.out.println("LoginController: Failed to change password for loginId: " + login_id);
            return ResponseEntity.status(400).body("Failed to change password.");
        }
    }

    // 프로필 정보를 조회
    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String loginId = jwtUtil.extractLoginId(token);
            System.out.println("LoginController: Fetching profile for loginId: " + loginId);
            Member member = loginService.getMemberById(loginId);
            if (member != null) {
                return ResponseEntity.ok("User profile: " + member.getNickname());
            } else {
                System.out.println("LoginController: User not found for loginId: " + loginId);
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (Exception e) {
            System.out.println("LoginController: Invalid token: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }
}
