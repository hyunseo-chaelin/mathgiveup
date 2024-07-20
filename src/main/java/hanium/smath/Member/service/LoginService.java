package hanium.smath.Member.service;
import com.google.cloud.firestore.*;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.LoginRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.concurrent.*;
import com.google.api.core.ApiFuture;

@Service
public class LoginService {

    private final Firestore firestore;
    private final LoginRepository loginRepository;

    @Autowired
    public LoginService(Firestore firestore, LoginRepository loginRepository) {
        this.firestore = firestore; // memberservice 클래스 생성될 때 firestore 객체 주입
        // System.out.println("MemberService instantiated with Firestore");
        this.loginRepository = loginRepository;
    }


    public Member getMemberById(String id) throws ExecutionException, InterruptedException, TimeoutException {
        try {
            Query query = firestore.collection("Members").whereEqualTo("login_id", id);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            // 타임아웃을 10초로 설정
            List<QueryDocumentSnapshot> documents = querySnapshot.get(100, TimeUnit.SECONDS).getDocuments();
            System.out.println("Documents found: " + documents.size());

            if (!documents.isEmpty()) {
                return documents.get(0).toObject(Member.class);
            } else {
                throw new IllegalArgumentException("Invalid login_id: " + id);
            }
        } catch (Exception ex) {    // 모든 종류의 예외
            System.err.println("Error retrieving member: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    public Member findByGoogleId(String googleId) throws ExecutionException, InterruptedException, TimeoutException {
        try {
            Query query = firestore.collection("Members").whereEqualTo("googleId", googleId);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.get(100, TimeUnit.SECONDS).getDocuments();

            if (!documents.isEmpty()) {
                return documents.get(0).toObject(Member.class);
            } else {
                return null;
            }
        } catch (Exception ex) {
            System.err.println("Error retrieving member by Google ID: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    public void save(Member member) throws ExecutionException, InterruptedException, TimeoutException {
        try {
            DocumentReference docRef = firestore.collection("Members").document(member.getGoogleId());
            ApiFuture<WriteResult> result = docRef.set(member);
            result.get(100, TimeUnit.SECONDS);
            System.out.println("Member saved with ID: " + member.getGoogleId());
        } catch (Exception ex) {
            System.err.println("Error saving member: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    public String findLoginId(String email, String birthDate) throws ExecutionException, InterruptedException {
        System.out.println("Searching for login ID with email: " + email + " and birthDate: " + birthDate);

        Member member = loginRepository.findByEmailAndBirthDate(email, birthDate);
        if (member != null) {
            System.out.println("Found member with login ID: " + member.getLogin_id());
            return member.getLogin_id();
        } else {
            System.out.println("No member found with provided email and birth date.");
            throw new RuntimeException("No member found with provided email and birth date.");
        }
    }

    public boolean checkUserExists(String loginId) throws ExecutionException, InterruptedException {
        System.out.println("Checking if user exists with login ID: " + loginId);
        Member member = loginRepository.findByLoginId(loginId);
        return member != null;
    }
}
