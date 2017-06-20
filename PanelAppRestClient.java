package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ml on 20/06/2017.
 */
public class PanelAppRestClient {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static PanelAppResponse getPanelAppResult(String gene) throws IOException {
        return objectMapper.readValue(new URL("https://bioinfo.extge.co.uk/crowdsourcing/WebServices/search_genes/" + gene + "/?&LevelOfConfidence=HighEvidence&format=json"), PanelAppResponse.class);
    }
}
