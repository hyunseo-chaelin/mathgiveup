package hanium.smath.Member.config;

import com.google.cloud.firestore.Firestore;
import hanium.smath.Member.security.JwtRequestFilter;
import hanium.smath.Member.service.CustomMemberDetailsService;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.authorization.AuthenticatedAuthorizationManager.anonymous;
import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final Firestore firestore;
    private final JwtRequestFilter jwtRequestFilter;

//    public SecurityConfig(Firestore firestore) {
//        this.firestore = firestore;
//        System.out.println("Firestore instance injected: " + firestore);
//    }

    public SecurityConfig(Firestore firestore, JwtRequestFilter jwtRequestFilter) {
        this.firestore = firestore;
        this.jwtRequestFilter = jwtRequestFilter;
        System.out.println("Firestore instance injected: " + firestore);
    }

//    @Autowired
//    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring SecurityFilterChain...");
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .cors(withDefaults()) // 추가된 CORS 설정
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/members/**").permitAll()
                                .requestMatchers("/api/community/**").hasRole("USER")
//                        .requestMatchers("/api/community/**").authenticated()
//                        .requestMatchers("/api/learning/**").authenticated() // 학습 관련 API에 인증 요구
                        .anyRequest().permitAll()
                )
                        .sessionManagement(sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션을 사용하지 않음

        // JwtRequestFilter를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .permitAll()
//                );

        System.out.println("SecurityFilterChain configured.");
        // JwtRequestFilter를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Configuring PasswordEncoder...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomMemberDetailsService CustomMemberDetailsService() {
        System.out.println("Creating CustomMemberDetailsService with Firestore: " + firestore);
        return new CustomMemberDetailsService(firestore);
    }
}