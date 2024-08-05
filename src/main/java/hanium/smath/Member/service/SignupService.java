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

    public void registerMember(String email, String name, String loginId, String loginPwd, String nickname, int grade, LocalDate birthdate, String phoneNum) {
        Member member = Member.builder()
                .email(email)
                .name(name)
                .loginId(loginId)
                .loginPwd(loginPwd)
                .nickname(nickname)
                .grade(grade)
                .birthdate(birthdate)
                .phoneNum(phoneNum)
                .idLevel(1) // 기본 레벨
                .isEmailVerified(false) // 기본적으로 이메일 인증되지 않은 상태
                .createTime(new Timestamp(System.currentTimeMillis()))
                .isAdmin(false) // 기본적으로 관리자가 아닌 상태
                .build();

        signupRepository.save(member);
    }
}
