//package hanium.smath.Community.util;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//import com.google.cloud.firestore.DocumentReference;
//
//import java.io.IOException;
//
//public class DocumentReferenceSerializer extends StdSerializer<DocumentReference> {
//
//    public DocumentReferenceSerializer() {
//        this(null);
//    }
//
//    public DocumentReferenceSerializer(Class<DocumentReference> t) {
//        super(t);
//    }
//
//    @Override
//    public void serialize(DocumentReference value, JsonGenerator gen, SerializerProvider provider) throws IOException {
//        gen.writeString(value.getPath());
//    }
//}
