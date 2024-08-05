package hanium.smath.Member.service;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.time.LocalDate;

@Service
public class LoginService {

    private final LoginRepository loginRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    // 로그인 ID를 통해 사용자를 조회
    public Member getMemberById(String loginId) {
        System.out.println("Fetching member by loginId: " + loginId);
        return loginRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login_id: " + loginId));
    }


    public Member findByGoogleId(String googleId) throws ExecutionException, InterruptedException, TimeoutException {
        // 이 부분은 Firebase에서 MySQL로 변경 필요
        System.out.println("Fetching member by googleId: " + googleId);
        return loginRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid google_id: " + googleId));
    }

    public void save(Member member) {
        System.out.println("Saving member with loginId: " + member.getLoginId());
        loginRepository.save(member);
    }

    public String findLoginId(String email, LocalDate birthdate) {
        System.out.println("Searching for loginId with email: " + email + " and birthdate: " + birthdate);
        return loginRepository.findByEmailAndBirthdate(email, birthdate)
                .orElseThrow(() -> new RuntimeException("No member found with provided email and birth date."))
                .getLoginId();
    }

    // 사용자가 존재하는지 확인
    public boolean checkUserExists(String loginId) {
        return loginRepository.findByLoginId(loginId).isPresent();
    }

    // 사용자의 비밀번호를 변경
    public boolean changeUserPassword(String loginId, String newPassword) {
        return loginRepository.findByLoginId(loginId)
                .map(member -> {
                    member.setLoginPwd(newPassword);
                    loginRepository.save(member);
                    return true;
                })
                .orElse(false);
    }
}
