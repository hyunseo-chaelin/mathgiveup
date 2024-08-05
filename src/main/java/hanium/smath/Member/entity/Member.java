package hanium.smath.Member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

//import javax.*;
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
    private Long idMember;

    @Column(name = "login_id", length = 30, nullable = true, unique = true)
    private String loginId;

    @Column(name = "login_pwd", length = 30, nullable = true)
    private String loginPwd;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "nickname", length = 30, nullable = false, unique = true)
    private String nickname;

    @Column(name = "grade", length = 10, nullable = false)
    private int grade;

    @Column(name = "id_level", nullable = false)
    private int idLevel;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;

    @Column(name = "icon_path", length = 255, nullable = true)
    private String iconPath;

    @Column(name = "phone_num", length = 30, nullable = false)
    private String phoneNum;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "google_id", nullable = true, unique = true)
    private String googleId; // 구글 아이디 추가
}

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//
//public class Member {
//    private String idMember;
//
//    private String name;
//    private String email;
//    private String login_id;
//    private String login_pwd;
//    private String nickname;
//    private String birthDate;
//    private int grade;
//    private boolean emailVerified;
//
//    private String googleId;
//}
