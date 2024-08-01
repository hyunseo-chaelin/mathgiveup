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
    @Column(name = "idMember")
    private Long idMember;

    @Column(name = "loginId", length = 30, nullable = false, unique = true)
    private String loginId;

    @Column(name = "loginPwd", length = 30, nullable = false)
    private String loginPwd;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "nickname", length = 30, nullable = false, unique = true)
    private String nickname;

    @Column(name = "grade", length = 10, nullable = false)
    private int grade;

    @Column(name = "idLevel", nullable = false)
    private int idLevel;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "isEmailVerified", nullable = false)
    private boolean isEmailVerified;

    @CreationTimestamp
    @Column(name = "createTime", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;

    @Column(name = "iconPath", length = 255, nullable = false)
    private String iconPath;

    @Column(name = "phoneNum", length = 30, nullable = false)
    private String phoneNum;

    @Column(name = "isAdmin", nullable = false)
    private boolean isAdmin;
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
