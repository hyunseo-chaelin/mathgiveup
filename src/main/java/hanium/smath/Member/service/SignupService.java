package hanium.smath.Member.service;
import com.google.cloud.firestore.*;
import hanium.smath.Member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.concurrent.*;
import com.google.api.core.ApiFuture;

@Service
public class SignupService {

    private final Firestore firestore;

    @Autowired
    public SignupService(Firestore firestore) {
        this.firestore = firestore; // memberservice 클래스 생성될 때 firestore 객체 주입
        // System.out.println("MemberService instantiated with Firestore");
    }

    public Member getMemberByNickname(String nickname) throws ExecutionException, InterruptedException {
        Query query = firestore.collection("Members").whereEqualTo("nickname", nickname);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            return documents.get(0).toObject(Member.class);
        }
        return null;
    }

    public String createMember(Member member) throws ExecutionException, InterruptedException, TimeoutException {
//        if (getMemberByEmail(member.getEmail()) != null) {
//            throw new IllegalArgumentException("Email already exists");
//        }

        if (getMemberByNickname(member.getNickname()) != null) {
            throw new IllegalArgumentException("Nickname already exists");
        }

        CollectionReference members = firestore.collection("Members");
        ApiFuture<DocumentReference> result = members.add(member);

        return result.get().getId();
    }

//    public String completeRegistration(Member member) throws ExecutionException, InterruptedException, TimeoutException {
//        Member existingMember = getMemberByEmail(member.getEmail());
//        if (existingMember != null) {
//            if (existingMember.isEmailVerified()) {
//                throw new IllegalArgumentException("This email is already registered. Please log in.");
//            } else {
//                throw new IllegalArgumentException("Email not verified");
//            }
//        }
//
//        if (getMemberByNickname(member.getNickname()) != null) {
//            throw new IllegalArgumentException("Nickname already exists");
//        }
//
//        // 새로운 회원 객체 생성
//        member.setEmailVerified(true);
//        save(member);
//
//        return member.getLogin_id();
//    }
}
