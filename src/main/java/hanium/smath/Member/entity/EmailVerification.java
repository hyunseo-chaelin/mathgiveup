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

    private int verificationCode;
    private boolean verifiedEmail;
    private LocalDateTime createTime;
}
