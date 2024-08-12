package hanium.smath.Member.controller;

import hanium.smath.Member.dto.KakaoLoginRequest; // KakaoLoginRequest 추가
import hanium.smath.Member.dto.GoogleLoginRequest;
import hanium.smath.Member.dto.AuthResponse;
import hanium.smath.Member.dto.KakaoProfile;
import hanium.smath.Member.dto.LoginRequest;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.security.JwtUtil;
import hanium.smath.Member.service.EmailService;
import hanium.smath.Member.service.GoogleLoginService;
import hanium.smath.Member.service.KakaoService;
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
    private final KakaoService kakaoService;

    @Autowired
    public LoginController(LoginService loginService, GoogleLoginService googleLoginService, JwtUtil jwtUtil, EmailService emailService, KakaoService kakaoService) {
        this.loginService = loginService; // controller가 생성될 때 service 주입하기
        System.out.println("MemberController instantiated with MemberService");
        this.googleLoginService = googleLoginService;
        System.out.println("MemberController instantiated with googleLoginService");
        this.jwtUtil = jwtUtil;
        System.out.println("MemberController instantiated with jwtUtil");
        this.emailService = emailService;
        System.out.println("MemberController instantiated with email");
        this.kakaoService = kakaoService;
        System.out.println("MemberController instantiated with kakaoService");
    }

    // 로그인
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

    // 카카오 로그인 추가 부분
    @PostMapping("/kakao-login")
    public ResponseEntity<String> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        try {
            System.out.println("Kakao access token received: " + request.getAccessToken());

            // 카카오 사용자 정보 가져오기
            KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(request.getAccessToken());

            // 사용자 정보로 로그인 처리
            Member member = loginService.processKakaoLogin(kakaoProfile);

            if (member != null) {
                // JWT 토큰 생성
                String token = jwtUtil.generateToken(member.getLoginId());
                System.out.println("Member found : " + member.getNickname());
                return ResponseEntity.ok("Login successful! Token: " + token);
            } else {
                System.out.println("Failed to process kakao login request");
                return ResponseEntity.status(401).body("Login failed for Kakao ID");
            }

        } catch (Exception e) {
            System.err.println("Error during Kakao login: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during Kakao login: " + e.getMessage());
        }
    }
    // 카카오 로그인 추가 끝

    // 아이디 찾기
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

    // 비밀번호 재설정 부분
    // 비번 재설정 - 아이디 찾기를 통해 회원의 존재 여부
    @PostMapping("/reset/password/loginId")
    public ResponseEntity<String> initiatePasswordReset(@RequestParam String login_id) {
        System.out.println("LoginController: Initiating password reset for loginId: " + login_id);
        try {
            Member member = loginService.getMemberById(login_id);
            if (member != null) {
                // 회원이 존재하면 이메일을 보낼지 여부를 사용자에게 확인하는 메시지 제공
                System.out.println("LoginController: Member found. Asking for email confirmation.");
                return ResponseEntity.ok("Account exists. Would you like to send a verification code to the registered email?");
            } else {
                System.out.println("LoginController: User not found for loginId: " + login_id);
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (Exception e) {
            System.err.println("Error during password reset initiation: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during password reset initiation.");
        }
    }

    // 로그인 ID로 인증 코드 전송 (재전송도 이 API 사용)
    @PostMapping("/reset/password/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String login_id) {
        System.out.println("LoginController: Initiating password reset for loginId: " + login_id);
        try {
            // 해당 login_id를 가진 사용자가 있는지 확인
            Member member = loginService.getMemberById(login_id);
            if (member != null) {
                // 기존 코드 무효화 및 새로운 인증 코드 생성 및 이메일 전송
                emailService.sendVerificationCode(login_id, member.getEmail());
                System.out.println("LoginController: Verification code sent to email: " + member.getEmail());
                return ResponseEntity.ok("Verification code sent.");
            } else {
                System.out.println("LoginController: User not found for loginId: " + login_id);
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (RuntimeException e) {
            System.err.println("Error during verification code sending: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during verification code sending: " + e.getMessage());
        }
    }

    // 이메일 보낸 코드 검증
    @PatchMapping("/reset/password/verify")
    public ResponseEntity<String> verifyCode(@RequestParam String login_id, @RequestParam int code) {
        System.out.println("LoginController: Verifying code for loginId: " + login_id + ", code: " + code);
        boolean codeValid = emailService.verifyEmailCode(login_id, code);
        if (codeValid) {
            // 상태 업데이트가 필요한 경우
            return ResponseEntity.ok("Verification code valid.");
        } else {
            System.out.println("LoginController: Invalid verification code for loginId: " + login_id);
            return ResponseEntity.status(400).body("Invalid verification code.");
        }
    }

    // 클라이언트 측(UI)에서 먼저 두 개의 비밀번호가 일치하는지를 확인한 후 서버로 요청을 보내는 것이 일반적
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

    // 이메일로 인증 코드를 전송하는 API
    @PatchMapping("find/loginId/send/email")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        System.out.println("EmailController: Sending verification code to email: " + email);
        try {
            emailService.sendEmail(email);
            return ResponseEntity.ok("Verification code sent to: " + email);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return ResponseEntity.status(500).body("Error sending verification code: " + e.getMessage());
        }
    }

    // 이메일로 보낸 인증 코드를 검증하는 API
    @PatchMapping("/find/loginId/email/verify")
    public ResponseEntity<String> verifyCodeFindId(@RequestParam String email, @RequestParam String code) {
        System.out.println("EmailVerificationController: Verifying code for email: " + email + ", code: " + code);
        int codeInt = Integer.parseInt(code);
        boolean codeValid = emailService.verifyEmailCodeByEmail(email, codeInt);
        if (codeValid) {
            return ResponseEntity.ok("Verification code valid.");
        } else {
            System.out.println("EmailVerificationController: Invalid verification code for email: " + email);
            return ResponseEntity.status(400).body("Invalid verification code.");
        }
    }

}
