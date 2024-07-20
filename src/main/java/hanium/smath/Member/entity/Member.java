package hanium.smath.Member.entity;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Member {
    private String name;
    private String email;
    private String login_id;
    private String login_pwd;
    private String nickname;
    private String birthDate;
    private int grade;
    private boolean emailVerified;

    private String googleId;
}
