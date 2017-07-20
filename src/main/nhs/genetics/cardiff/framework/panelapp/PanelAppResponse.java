package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for PanelApp object
 */
public class PanelAppResponse {

    @JsonProperty("results")
    private Result[] results;

    @JsonProperty("meta")
    private Meta meta;

    public Result[] getResults() {
        return results;
    }
    public Meta getMeta() {
        return meta;
    }

}
