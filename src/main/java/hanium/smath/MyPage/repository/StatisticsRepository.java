//package hanium.smath.MyPage.repository;
//
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.QueryDocumentSnapshot;
//import com.google.cloud.firestore.QuerySnapshot;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//// 데이터를 조회하거나 저장하는 기능.
//@Repository
//public class StatisticsRepository {
//
//    @Autowired
//    private Firestore firestore;
//
//    public CompletableFuture<List<QueryDocumentSnapshot>> findGameSessions(String idMember) {
//        System.out.println("Finding game sessions for user: " + idMember);
//
//        DocumentReference memberRef = firestore.collection("Members").document(idMember);
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                QuerySnapshot querySnapshot = firestore.collection("GameSessions")
//                        .whereEqualTo("idMember", memberRef)
//                        .get()
//                        .get();
//                System.out.println("Found " + querySnapshot.getDocuments().size() + " game sessions for user: " + idMember);
//
//                return querySnapshot.getDocuments();
//            } catch (InterruptedException | ExecutionException e) {
//                System.err.println("Error fetching game sessions: " + e.getMessage());
//                throw new RuntimeException(e);
//            }
//        });
//    }
//}