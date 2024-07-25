package hanium.smath.Member.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.protobuf.Api;
import hanium.smath.Member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.Random;

@Service
public class SignupService {
    private final Firestore firestore;
    private final EmailService emailService;

    @Autowired
    public SignupService(Firestore firestore, EmailService emailService) {
        this.firestore = firestore;
        this.emailService = emailService;
    }

    public boolean checkEmailExists(String email) throws ExecutionException, InterruptedException {
        System.out.println("SignupService: Checking if email exists: " + email);
        CollectionReference members = firestore.collection("Members");
        ApiFuture<QuerySnapshot> future = members.whereEqualTo("email", email).get();
        boolean exists = !future.get().isEmpty();
        System.out.println("SignupService: Email exists: " + exists);
        return exists;
    }

    public boolean checkLoginIdExists(String loginId) throws ExecutionException, InterruptedException {
        System.out.println("SignupService: Checking if loginId exists: " + loginId);
        CollectionReference members = firestore.collection("Members");
        ApiFuture<QuerySnapshot> future = members.whereEqualTo("login_id", loginId).get();
        boolean exists = !future.get().isEmpty();
        System.out.println("SignupService: LoginId exists: " + exists);
        return exists;
    }

    public void registerMember(Member member) {
        System.out.println("SignupService: Registering member: " + member);
        CollectionReference members = firestore.collection("Members");
        DocumentReference docRef = members.document();
        docRef.set(member);
        System.out.println("SignupService: Member registered: " + member);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // 4자리 랜덤 숫자 생성
        return String.valueOf(code);
    }


    public void sendVerificationCode(String email) throws ExecutionException, InterruptedException {
        System.out.println("SignupService: Sending verification code to email: " + email);
        String code = generateVerificationCode();
        emailService.sendEmail(email, "Verification Code", "Your verification code is: " + code);
        saveVerificationCode(email, code);
        System.out.println("SignupService: Verification code sent: " + code);
    }

    private void saveVerificationCode(String email, String code) {
        System.out.println("SignupService: Saving verification code for email: " + email + ", code: " + code);
        CollectionReference verificationCodes = firestore.collection("VerificationCodes");
        Map<String, Object> data = new HashMap<>();
//        data.put("email", email);
        data.put("code", code);
        verificationCodes.document(email).set(data);
        System.out.println("SignupService: Verification code saved");
    }

    public boolean verifyEmailCode(String email, String code) throws ExecutionException, InterruptedException {
        System.out.println("SignupService: Verifying email code for email: " + email + ", code: " + code);
        CollectionReference verificationCodes = firestore.collection("VerificationCodes");
        DocumentReference docRef = verificationCodes.document(email);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            String storedCode = document.getString("code");
            boolean verified = code.equals(storedCode);
            System.out.println("SignupService: Email code verification result: " + verified);
            return verified;
        } else {
            System.out.println("SignupService: Email code verification failed: Document does not exist");
            return false;
        }
    }


}
