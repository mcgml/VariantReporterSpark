package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * REST client for PanelApp
 */
public class PanelAppRestClient {
    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

    public static PanelAppResponse searchByGene(String gene) throws IOException {
        return objectMapper.readValue(new URL(
                "https://bioinfo.extge.co.uk/crowdsourcing/WebServices/search_genes/" + gene + "?format=json"
        ), PanelAppResponse.class);
    }
}
