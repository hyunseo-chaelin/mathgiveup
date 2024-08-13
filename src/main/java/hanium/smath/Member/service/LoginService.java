package hanium.smath.Member.service;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import hanium.smath.Member.dto.KakaoProfile;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
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

    public boolean changeUserPassword(String email, String newPassword) {
        return loginRepository.findByEmail(email)
                .map(member -> {
                    member.setLoginPwd(newPassword);
                    loginRepository.save(member);
                    return true;
                })
                .orElse(false);
    }

    // 카카오 ID로 사용자를 조회하는 메서드 추가
    public Member findByKakaoId(String kakaoId) {
        System.out.println("Fetching member by kakaoId: " + kakaoId);
        return loginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid kakao_id: " + kakaoId));
    }

    // 카카오 사용자 정보를 기반으로 회원을 생성하고 저장하는 메서드 추가
    public Member processKakaoLogin(KakaoProfile kakaoProfile) {
        String kakaoId = String.valueOf(kakaoProfile.getId());

        // 카카오 ID로 기존 사용자가 있는지 확인
        return loginRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    // 존재하지 않으면 새로운 회원 생성
                    Member newMember = new Member();
                    newMember.setKakaoId(kakaoId);
                    newMember.setLoginId(kakaoId); // 이 부분은 적절히 수정 필요
                    newMember.setEmail(kakaoProfile.getKakaoAccount().getEmail());
                    newMember.setNickname(kakaoProfile.getKakaoAccount().getProfile().getNickname());
                    // 필요한 추가 정보 설정
                    save(newMember);
                    return newMember;
                });
    }
}
