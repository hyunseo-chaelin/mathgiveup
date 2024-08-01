package hanium.smath.Member.service;

//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.*;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.ExecutionException;

@Service
public class CustomMemberDetailsService implements UserDetailsService {

    private final LoginRepository loginRepository;

    @Autowired
    public CustomMemberDetailsService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = loginRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + username));

            List<GrantedAuthority> authorities = new ArrayList<>();
            // 기본 권한을 USER로 설정
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            System.out.println("User found: " + member.getLoginId() + ", with authorities: " + authorities);

        // UserDetails 객체를 생성하여 반환
        return new User(member.getLoginId(), member.getLoginPwd(), authorities);
    }

//    @Override
//    public UserDetails loadUserByUsername(String login_id) throws UsernameNotFoundException {
//        try {
//            System.out.println("Fetching member with login_id: " + login_id);
//            Query query = firestore.collection("Members").whereEqualTo("login_id", login_id);
//            QuerySnapshot querySnapshot = query.get().get();
//
//            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
//            if (documents.isEmpty()) {
//                System.out.println("User not found with login_id: " + login_id);
//                throw new UsernameNotFoundException("User not found");
//            }
//
//            Member member = documents.get(0).toObject(Member.class);
//            System.out.println("User found: " + member.getLogin_id());
//
//            return User.builder()
//                    .username(member.getLogin_id())
//                    .password(member.getLogin_pwd())
//                    .build();
//        } catch (InterruptedException | ExecutionException e) {
//            System.err.println("Error fetching member: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
}
