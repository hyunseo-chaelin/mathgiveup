package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String message;
    private String nickname;
    private String token;

    public LoginResponse(String message, String nickname, String token) {
        this.message = message;
        this.nickname = nickname;
        this.token = token;
    }
}
