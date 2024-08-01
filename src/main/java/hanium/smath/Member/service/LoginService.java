package hanium.smath.Member.service;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.*;
import java.time.LocalDate;

@Service
public class LoginService {

    private final LoginRepository loginRepository;

    @Autowired
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }


    public Member getMemberById(String loginId) {
        System.out.println("Fetching member by loginId: " + loginId);
        return loginRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login_id: " + loginId));
    }


//    public Member findByGoogleId(String googleId) throws ExecutionException, InterruptedException, TimeoutException {
//        try {
//            Query query = firestore.collection("Members").whereEqualTo("googleId", googleId);
//            ApiFuture<QuerySnapshot> querySnapshot = query.get();
//
//            List<QueryDocumentSnapshot> documents = querySnapshot.get(100, TimeUnit.SECONDS).getDocuments();
//
//            if (!documents.isEmpty()) {
//                return documents.get(0).toObject(Member.class);
//            } else {
//                return null;
//            }
//        } catch (Exception ex) {
//            System.err.println("Error retrieving member by Google ID: " + ex.getMessage());
//            ex.printStackTrace();
//            throw ex;
//        }
//    }

    public void save(Member member) {
        System.out.println("Saving member with loginId: " + member.getLoginId());
        loginRepository.save(member);
    }

    public String findLoginId(String email, LocalDate birthdate) {
        System.out.println("Searching for loginId with email: " + email + " and birthdate: " + birthdate);
        return loginRepository.findByEmailAndBirthdate(email, birthdate)
                .orElseThrow(() -> new RuntimeException("No member found with provided email and birth date."))
                .getLoginId();
    }

    public boolean checkUserExists(String loginId) {
        System.out.println("Checking if user exists with loginId: " + loginId);
        return loginRepository.findByLoginId(loginId).isPresent();
    }
}
