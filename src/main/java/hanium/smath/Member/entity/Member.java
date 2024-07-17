package hanium.smath.Member.entity;

import lombok.*;

import java.time.LocalDate;

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
    private LocalDate birthDate;
    private int grade;
    private boolean emailVerified;

    private String googleId;
}
