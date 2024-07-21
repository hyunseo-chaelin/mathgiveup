package hanium.smath.Member.controller;

import hanium.smath.Member.dto.AuthResponse;
import hanium.smath.Member.dto.LoginRequest;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.security.JwtUtil;
import hanium.smath.Member.service.GoogleLoginService;
import hanium.smath.Member.service.LoginService;

import java.util.Map;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/members")
public class LoginController {

    private LoginService loginService;
    private final GoogleLoginService googleLoginService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginController(LoginService loginService, GoogleLoginService googleLoginService, JwtUtil jwtUtil) {
        this.loginService = loginService; // controller가 생성될 때 service 주입하기
        System.out.println("MemberController instantiated with MemberService");
        this.googleLoginService = googleLoginService;
        System.out.println("MemberController instantiated with googleLoginService");
        this.jwtUtil = jwtUtil;
        System.out.println("MemberController instantiated with jwtUtil");
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
                Member infoMember = loginService.getMemberById(loginRequest.getLogin_id());

                if (infoMember != null) {
                    System.out.println("Member found : " + loginRequest.getLogin_id());
                    if (infoMember.getLogin_pwd().equals(loginRequest.getLogin_pwd())) {
                        String token;
                        if (loginRequest.isAutoLogin()) {
                            System.out.println("Generating token with extended expiry for ID: " + loginRequest.getLogin_id());
                            token = jwtUtil.generateTokenWithExtendedExpiry(infoMember.getLogin_id());
                        } else {
                            System.out.println("Generating token for ID: " + loginRequest.getLogin_id());
                            token = jwtUtil.generateToken(infoMember.getLogin_id());
                        }
                        AuthResponse response = new AuthResponse("Login successful", infoMember.getNickname(), token);

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
            String login_id = loginService.findLoginId(email,birthDate);
            System.out.println("Found ID: " + login_id);
            return ResponseEntity.ok("Found ID! ID: " + login_id);
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            System.err.println("Error finding login ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset/password")
    public ResponseEntity resetPassword(@RequestParam String login_id) {
        try {
            boolean userExists = loginService.checkUserExists(login_id);
            if (userExists) {
                System.out.println("Find");
                return ResponseEntity.ok("User exists. Proceed to send reset email.");
            } else {
                System.out.println("No Found");
                return ResponseEntity.status(302).header("Location", "/api/members/find/loginId").build();
            }
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            return ResponseEntity.status(500).body("Error occurred while checking user existence: " + e.getMessage());
        }
    }

    // 자동로그인 token 저장이 잘 되었는지 확인하는 api임.
    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String loginId = jwtUtil.extractLoginId(token);
            Member member = loginService.getMemberById(loginId);
            if (member != null) {
                return ResponseEntity.ok("User profile: " + member.getNickname());
            } else {
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }

}
