//package hanium.smath.Member.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//@Service
//public class EmailService {
//    private final JavaMailSender javaMailSender;
//
//    @Autowired
//    public EmailService(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//    public void sendEmail(String to, String subject, String text) {
//        System.out.println("EmailService: Preparing to send email to: " + to);
//        MimeMessage message = javaMailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text, true);
//            javaMailSender.send(message);
//            System.out.println("EmailService: Email sent successfully to: " + to);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.out.println("EmailService: Failed to send email to: " + to);
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
//}
