package hanium.smath.Member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "emailverification")
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmailVerification;

    @ManyToOne
    @JoinColumn(name = "login_id", referencedColumnName = "login_id")
    private Member member;

    private String email;
    private int verificationCode;
    private boolean verifiedEmail;
    private LocalDateTime createTime;

    // 기본 생성자
    public EmailVerification() {
        this.verifiedEmail = false; // 새 인증 항목에 대한 기본값
        this.createTime = LocalDateTime.now(); // 생성 시간을 현재 시간으로 설정
    }

    // 편의를 위한 파라미터 생성자
    public EmailVerification(Member member, int verificationCode) {
        this.verifiedEmail = false; // 새 인증 항목에 대한 기본값
        this.createTime = LocalDateTime.now(); // 생성 시간을 현재 시간으로 설정

    }
}
