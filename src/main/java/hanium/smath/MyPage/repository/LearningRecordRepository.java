//package hanium.smath.MyPage.repository;
//
//import com.google.cloud.firestore.DocumentReference;
//import hanium.smath.MyPage.entity.LearningRecord;
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.QueryDocumentSnapshot;
//import com.google.cloud.firestore.QuerySnapshot;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.stream.Collectors;
//import java.time.format.DateTimeFormatter;
//
//@Repository
//public class LearningRecordRepository {
//
//    @Autowired
//    private Firestore firestore;
//    public static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//
//    public List<LearningRecord> findByMemberIdAndLearningDateBetween(DocumentReference idMember, LocalDate startDate, LocalDate endDate) throws ExecutionException, InterruptedException {
//        System.out.println("Querying LearningRecords for member: " + idMember + " between dates: " + startDate + " and " + endDate);
//
//        String startDateString = startDate.minusDays(7).format(formatter); // 이전 달 데이터를 포함하여 조회
//        String endDateString = endDate.plusDays(7).format(formatter); // 다음 달 데이터를 포함하여 조회
//
//        ApiFuture<QuerySnapshot> future = firestore.collection("LearningRecords")
//                .whereEqualTo("idMember", idMember)
//                .whereGreaterThanOrEqualTo("learningDate", startDateString)
//                .whereLessThanOrEqualTo("learningDate", endDateString)
//                .get();
//
//        System.out.println("Firestore query submitted.");
//
//        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//
//        System.out.println("Firestore query completed. Number of documents retrieved: " + documents.size());
//
//        List<LearningRecord> records = documents.stream()
//                .map(doc -> {
//                    LearningRecord record = doc.toObject(LearningRecord.class);
//                    String learningDateString = doc.getString("learningDate");
//
//                    if (learningDateString != null) {
//                        LocalDate learningDate = LocalDate.parse(learningDateString, formatter);
//                        record.setLearningDate(learningDateString); // 여전히 String으로 저장
//                        System.out.println("Document ID: " + doc.getId() + ", LearningDate: " + learningDate);
//                    } else {
//                        System.err.println("Warning: learningDate is null for document ID: " + doc.getId());
//                    }
//                    System.out.println("Document ID: " + doc.getId() + ", LearningRecord: " + record);
//                    return record;
//                })
//                .collect(Collectors.toList());
//
//        System.out.println("Conversion to LearningRecord objects completed. Number of records: " + records.size());
//
//        return records;
//    }
//}
