package hanium.smath.Member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String message;
    private String nickname;
    private String token;

    public AuthResponse(String message, String nickname, String token) {
        this.message = message;
        this.nickname = nickname;
        this.token = token;
    }
}
