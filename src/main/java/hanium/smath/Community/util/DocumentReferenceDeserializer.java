//package hanium.smath.Community.util;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import com.google.cloud.firestore.DocumentReference;
//import com.google.cloud.firestore.Firestore;
//import com.google.firebase.cloud.FirestoreClient;
//
//import java.io.IOException;
//
//public class DocumentReferenceDeserializer extends StdDeserializer<DocumentReference> {
//
//    public DocumentReferenceDeserializer() {
//        this(null);
//    }
//
//    public DocumentReferenceDeserializer(Class<?> vc) {
//        super(vc);
//    }
//
//    @Override
//    public DocumentReference deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        JsonNode node = p.getCodec().readTree(p);
//        String path = node.asText();
//        Firestore firestore = FirestoreClient.getFirestore();
//        return firestore.document(path);
//    }
//}
