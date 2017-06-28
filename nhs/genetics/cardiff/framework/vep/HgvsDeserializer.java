package nhs.genetics.cardiff.framework.vep;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Custom Jackson deserializer for VEP output
 */
public class HgvsDeserializer extends StdDeserializer<String> {

    public HgvsDeserializer() {
        this(null);
    }

    public HgvsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode = objectCodec.readTree(jsonParser);

        String value = jsonNode.asText().trim();

        if (value.isEmpty()) {
            return null;
        }

        if (value.contains("p.=")){
            return "p.=";
        }

        return value.split(":")[1];
    }
}
