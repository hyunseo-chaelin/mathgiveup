package hanium.smath.Member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Member")
    private int idMember;

    @Column(name="email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "login_id", length = 30, nullable = true, unique = true)
    private String loginId;

    @Column(name = "login_pwd", length = 300, nullable = true)
    private String loginPwd;

    @Column(name = "name", length = 30, nullable = true)
    private String name;

    @Column(name = "nickname", length = 30, nullable = true, unique = true)
    private String nickname;

    @Column(name = "grade", length = 10, nullable = true)
    private Integer grade;

    @Column(name = "id_level", nullable = false)
    private int idLevel;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name="icon", nullable = false)
    private String icon;

    @Column(name = "google_id", nullable = true, unique = true)
    private String googleId; // 구글 아이디 추가

    @Column(name = "kakaoId", nullable = true, unique = true)
    private String kakaoId; // 구글 아이디 추가
}
