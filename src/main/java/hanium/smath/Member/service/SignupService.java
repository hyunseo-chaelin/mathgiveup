package hanium.smath.Member.service;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;

@Service
public class SignupService {

    private final SignupRepository signupRepository;

    @Autowired
    public SignupService(SignupRepository signupRepository) {
        this.signupRepository = signupRepository;
    }

    public boolean checkEmailExists(String email) {
        return signupRepository.existsByEmail(email);
    }

    public boolean checkLoginIdExists(String loginId) {
        return signupRepository.existsByLoginId(loginId);
    }

    public void saveEmail(String email) {
        if (!signupRepository.existsByEmail(email)) {
            Member member = Member.builder()
                    .email(email)
                    .isEmailVerified(false)
                    .createTime(new Timestamp(System.currentTimeMillis()))
                    .idLevel(1)
                    .isAdmin(false)
                    .name("Default Name") // 기본값 설정
                    .build();
            signupRepository.save(member);
        }
    }

    public void markEmailAsVerified(String email) {
        Member member = signupRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found: " + email));
        member.setEmailVerified(true);
        signupRepository.save(member);
    }

    public boolean isEmailVerified(String email) {
        return signupRepository.findByEmail(email)
                .map(Member::isEmailVerified)
                .orElse(false);
    }

    public void registerMember(String email, String name, String loginId, String loginPwd, String nickname, int grade, LocalDate birthdate, String phoneNum) {
        Member member = signupRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found: " + email));

        member.setName(name);
        member.setLoginId(loginId);
        member.setLoginPwd(loginPwd);
        member.setNickname(nickname);
        member.setGrade(grade);
        member.setBirthdate(birthdate);
        member.setPhoneNum(phoneNum);
        signupRepository.save(member);
    }
}
