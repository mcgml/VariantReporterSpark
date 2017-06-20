package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ml on 20/06/2017.
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
