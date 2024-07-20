package hanium.smath.Member.service;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    private final SignupRepository signupRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignupService(SignupRepository signupRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.signupRepository = signupRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkEmailExists(String email) {
        return signupRepository.existsByEmail(email);
    }

    public void sendVerificationCode(String email) {
        String code = emailService.generateVerificationCode();
        emailService.saveVerificationCode(email, code);
        emailService.sendVerificationCode(email, code);
    }

    public boolean verifyEmailCode(String email, String code) {
        return emailService.verifyCode(email, code);
    }

    public Member registerMember(Member member) {
        member.setLogin_pwd(passwordEncoder.encode(member.getLogin_pwd()));
        member.setEmailVerified(true);
        return signupRepository.save(member);
    }
}
