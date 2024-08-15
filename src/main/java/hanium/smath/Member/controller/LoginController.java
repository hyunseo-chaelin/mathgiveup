package hanium.smath.Member.controller;

import hanium.smath.Member.dto.KakaoLoginRequest; // KakaoLoginRequest 추가
import hanium.smath.Member.dto.GoogleLoginRequest;
import hanium.smath.Member.dto.LoginResponse;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.dto.KakaoProfile;
import hanium.smath.Member.dto.LoginRequest;
import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.security.JwtUtil;
import hanium.smath.Member.service.EmailService;
import hanium.smath.Member.service.GoogleLoginService;
import hanium.smath.Member.service.KakaoService;
import hanium.smath.Member.service.LoginService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;


@RestController
@RequestMapping("/api/members")
public class LoginController {

    private LoginService loginService;
    private final GoogleLoginService googleLoginService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final KakaoService kakaoService;
    private final EmailVerificationRepository emailVerificationRepository;

    @Autowired
    public LoginController(LoginService loginService, GoogleLoginService googleLoginService, JwtUtil jwtUtil, EmailService emailService, KakaoService kakaoService, EmailVerificationRepository emailVerificationRepository) {
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
        this.emailVerificationRepository = emailVerificationRepository;
        System.out.println("MemberController instantiated with emailVerificationRepository");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("RequestBody received: " + loginRequest);

        if (loginRequest.getLogin_id() == null || loginRequest.getLogin_id().isEmpty()) {
            System.out.println("Login_id is empty.");
            return ResponseEntity.badRequest().body(new LoginResponse("Login_id is empty.", null, null));
        }

        if (loginRequest.getLogin_pwd() == null || loginRequest.getLogin_pwd().isEmpty()) {
            System.out.println("Login_pwd is empty.");
            return ResponseEntity.badRequest().body(new LoginResponse("Login_pwd is empty.", null, null));
        }

        try {
            System.out.println("Login request received for ID: " + loginRequest.getLogin_id());
            Member member = loginService.getMemberById(loginRequest.getLogin_id());

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
                    LoginResponse response = new LoginResponse("Login successful", member.getNickname(), token);

                    System.out.println("Login successful for ID: " + loginRequest.getLogin_id());
                    return ResponseEntity.ok(response);
                } else {
                    System.out.println("Invalid login_pwd for ID: " + loginRequest.getLogin_id());
                    return ResponseEntity.status(401).body(new LoginResponse("Invalid login_pwd.", null, null));
                }
            } else {
                System.out.println("Invalid login_id: " + loginRequest.getLogin_id());
                return ResponseEntity.status(401).body(new LoginResponse("Invalid login_id.", null, null));
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body(new LoginResponse("Error during login: " + e.getMessage(), null, null));
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            System.out.println("ID Token received: " + request.getIdToken());

            Member member = googleLoginService.processGoogleLogin(request);

            if (member != null) {
                System.out.println("Member found : " + member.getNickname());
                String token = jwtUtil.generateToken(member.getLoginId());
                LoginResponse response = new LoginResponse("Login successful", member.getNickname(), token);
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Failed to process Google login request");
                return ResponseEntity.status(401).body(new LoginResponse("Login failed for Google ID", null, null));
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body(new LoginResponse("Error during login: " + e.getMessage(), null, null));
        }
    }


    @PostMapping("/kakao-login")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
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
                LoginResponse response = new LoginResponse("Login successful", member.getNickname(), token);
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Failed to process Kakao login request");
                return ResponseEntity.status(401).body(new LoginResponse("Login failed for Kakao ID", null, null));
            }

        } catch (Exception e) {
            System.err.println("Error during Kakao login: " + e.getMessage());
            return ResponseEntity.status(500).body(new LoginResponse("Error during Kakao login: " + e.getMessage(), null, null));
        }
    }


    // 아이디 찾기
    @GetMapping("/find/loginId")
    public ResponseEntity<String> findLoginId(@RequestParam String email, @RequestParam String birthdate) {
        System.out.println("Received request to find login ID");
        try {
            LocalDate birthDateLocal = LocalDate.parse(birthdate);
            String loginId = loginService.findLoginId(email, birthDateLocal);
            System.out.println("Found ID: " + loginId);
            return ResponseEntity.ok(loginId);  // 로그인 ID를 직접 반환
        } catch (RuntimeException e) {
            System.err.println("Error finding login ID: " + e.getMessage());
            return ResponseEntity.badRequest().body("Login ID not found.");
        }
    }

    // 아이디찾기 - 이메일 보내기
    @PatchMapping("/find/send/email")
    public ResponseEntity<String> updateVerificationCode(@RequestParam String email) {
        System.out.println("EmailController: Updating verification code for email: " + email);
        try {
            // 새로운 인증 코드 생성
            int newCode = emailService.generateVerificationCode();

            // 이메일로 기존의 인증 레코드를 가져옴
            Optional<EmailVerification> optionalVerification = emailVerificationRepository.findTopByEmailOrderByCreateTimeDesc(email);


            if (optionalVerification.isPresent()) {
                // 기존 레코드가 있으면 인증번호만 업데이트
                EmailVerification existingVerification = optionalVerification.get();
                existingVerification.setVerificationCode(newCode);
                existingVerification.setCreateTime(LocalDateTime.now()); // 생성 시간 갱신
                emailVerificationRepository.save(existingVerification);
                System.out.println("EmailController: Updated verification code for email: " + email);

                // 인증 코드 이메일로 전송
                emailService.sendVerificationEmail(email, newCode);

                return ResponseEntity.ok("Verification code updated and sent to: " + email);
            } else {
                return ResponseEntity.status(404).body("No verification record found for email: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error updating verification code: " + e.getMessage());
            return ResponseEntity.status(500).body("Error updating verification code: " + e.getMessage());
        }
    }


    // 이메일로 보낸 인증 코드를 검증하는 API
    @GetMapping("/find/email/verify")
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

    // 비번 재설정 - 아이디 찾기를 통해 회원의 존재 여부 확인 및 이메일 반환
    @PostMapping("/reset/password/loginId")
    public ResponseEntity<String> initiatePasswordReset(@RequestParam String login_id) {
        System.out.println("LoginController: Initiating password reset for loginId: " + login_id);
        try {
            Member member = loginService.getMemberById(login_id);
            if (member != null) {
                // 회원이 존재하면 이메일을 반환
                String email = member.getEmail();
                System.out.println("LoginController: Member found. Email: " + email);
                return ResponseEntity.ok(email); // 이메일을 직접 반환
            } else {
                System.out.println("LoginController: User not found for loginId: " + login_id);
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (Exception e) {
            System.err.println("Error during password reset initiation: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during password reset initiation.");
        }
    }

    @PatchMapping("/reset/password/change")
    public ResponseEntity<String> changePassword(@RequestParam String email, @RequestParam String new_password, @RequestParam String code) {
        System.out.println("LoginController: Changing password for email: " + email + ", code: " + code);
        int codeInt = Integer.parseInt(code);
        // 인증 코드 검증 (verifiedEmail 필드 상태를 무시하고 검증)
        boolean codeValid = emailService.verifyEmailCodeByEmail(email, codeInt);
        if (!codeValid) {
            System.out.println("LoginController: Invalid verification code for email: " + email);
            return ResponseEntity.status(400).body("Invalid verification code.");
        }

        // 비밀번호 변경
        boolean passwordChanged = loginService.changeUserPassword(email, new_password);
        if (passwordChanged) {
            // 인증 코드 무효화 (실제로는 verifiedEmail을 true로 설정)
            emailService.invalidateVerificationCode(email, code);
            System.out.println("LoginController: Password changed successfully for email: " + email);
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            System.out.println("LoginController: Failed to change password for email: " + email);
            return ResponseEntity.status(400).body("Failed to change password.");
        }
    }
}
