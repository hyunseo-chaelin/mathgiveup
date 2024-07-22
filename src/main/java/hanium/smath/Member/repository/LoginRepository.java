package hanium.smath.Member.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import hanium.smath.Member.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class LoginRepository {

    public Member findByEmailAndBirthDate(String email, String birthDate) throws ExecutionException, InterruptedException {
        System.out.println("Starting findByEmailAndBirthDate method");

        Firestore firestore = FirestoreClient.getFirestore();
        System.out.println("Firestore instance acquired");

        ApiFuture<QuerySnapshot> future = firestore.collection("Members")
                .whereEqualTo("email", email)
                .whereEqualTo("birthDate", birthDate)
                .get();
        System.out.println("Query sent to Firestore");

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        System.out.println("Query result received. Number of documents: " + documents.size());

        if (documents.isEmpty()) {
            System.out.println("No user found.");
            throw new RuntimeException("No user found.");
        }

        for (QueryDocumentSnapshot document : documents) {
            Member member = document.toObject(Member.class);
            System.out.println("Checking document: " + document.getId());
            if (member.getEmail().equals(email) && member.getBirthDate().equals(birthDate)) {
                System.out.println("Found matching member: " + member.getLogin_id());
                return member;
            }
        }
        System.out.println("Email matches, but birth date doesn't match.");
        throw new RuntimeException("Email matches, but birth date doesn't match.");
    }

    public Member findByLoginId(String loginId) throws ExecutionException, InterruptedException {
        System.out.println("Starting findByLoginId method");

        Firestore firestore = FirestoreClient.getFirestore();
        System.out.println("Firestore instance acquired");

        ApiFuture<QuerySnapshot> future = firestore.collection("Members")
                .whereEqualTo("login_id", loginId)
                .get();
        System.out.println("Query sent to Firestore");

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        System.out.println("Query result received. Number of documents: " + documents.size());

        if (documents.isEmpty()) {
            System.out.println("No user found.");
            return null;
        }

        System.out.println("No matching member found for login ID: " + loginId);
        return documents.get(0).toObject(Member.class);
    }
}