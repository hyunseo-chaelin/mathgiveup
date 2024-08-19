package hanium.smath.Member.controller;

import hanium.smath.Member.dto.*;
import hanium.smath.Member.security.JwtUtil;
import hanium.smath.Member.security.JwtRequestFilter;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.service.SignupService;
import hanium.smath.Member.service.EmailService;
import hanium.smath.Member.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class SignupController {

    @Autowired
    private SignupService signupService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/check-loginId")
    public ResponseEntity<String> checkLoginId(@RequestParam String loginId) {
        if (signupService.checkLoginIdExists(loginId)) {
            return ResponseEntity.badRequest().body("Login ID already exists");
        } else {
            return ResponseEntity.ok("Login ID is available");
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        if (signupService.checkEmailExists(email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        } else {
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
                signupService.sendVerificationCodeToEmail(email);
                return ResponseEntity.ok("Email is available. Verification code sent to email.");
            }
        }
    }

    @PatchMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        int codeInt = Integer.parseInt(code);
        boolean isVerified = signupService.verifyEmailCode(email, codeInt);

        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> registerMember(@RequestBody SignupRequest signupRequest) {
        // 이메일 인증 정보 가져오기
        EmailVerification optionalEmailVerification = emailVerificationRepository.findEmailVerificationByEmail(signupRequest.getEmail());

        if (optionalEmailVerification == null || !optionalEmailVerification.isVerifiedEmail()) {
            return ResponseEntity.badRequest().body(null);
        }

        if (signupService.checkLoginIdExists(signupRequest.getLoginId())) {
            return ResponseEntity.badRequest().body(null);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getLoginPwd());
        signupRequest.setLoginPwd(encodedPassword);

        // 회원 등록
        signupService.registerMember(signupRequest);

        // 회원 정보 가져오기 - 회원가입 후 자동 로그인
        Member member = loginService.getMemberById(signupRequest.getLoginId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(member.getLoginId());

        // 응답 반환 - 토큰만 반환
        SignupResponse response = new SignupResponse(token);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMember() {
        signupService.deleteCurrentUser();
        return ResponseEntity.ok("Your account has been deleted.");
    }

    @PatchMapping("/change_nickname")
    public ResponseEntity<String> changeNickname(@RequestBody NicknameChangeRequest request) {
        signupService.changeNickname(request.getNewNickname());
        return ResponseEntity.ok("Nickname has been changed successfully.");
    }

    @PatchMapping("/change_icon")
    public ResponseEntity<String> changeIcon(@RequestBody IconChangeRequest request) {
        signupService.changeIcon(request.getNewIcon());
        return ResponseEntity.ok("Icon has been changed successfully.");
    }
}
