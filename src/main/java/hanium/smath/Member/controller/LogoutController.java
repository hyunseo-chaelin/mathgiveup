//package hanium.smath.Member.controller;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.http.ResponseEntity;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@RestController
//@RequestMapping("/api/members")
//public class LogoutController {
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        if (authentication != null) {
//            new SecurityContextLogoutHandler().logout(request, response, authentication);
//        }
//        return ResponseEntity.ok("Logout successful");
//    }
//}
