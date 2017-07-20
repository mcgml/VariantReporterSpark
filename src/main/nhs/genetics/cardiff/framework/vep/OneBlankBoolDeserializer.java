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
public class OneBlankBoolDeserializer extends StdDeserializer<Boolean> {

    public OneBlankBoolDeserializer() {
        this(null);
    }

    public OneBlankBoolDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode = objectCodec.readTree(jsonParser);

        String value = jsonNode.asText().trim();

        if (value.isEmpty()) {
            return null;
        }

        return value.equals("1");
    }
}