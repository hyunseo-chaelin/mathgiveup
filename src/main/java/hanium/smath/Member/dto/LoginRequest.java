package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginRequest {
    private String loginId;
    private String loginPwd;
    private boolean autoLogin;

    public LoginRequest() {}

    public LoginRequest(String loginId, String loginPwd, boolean autoLogin) {
        this.loginId = loginId;
        this.loginPwd = loginPwd;
        this.autoLogin = autoLogin;
    }

}
