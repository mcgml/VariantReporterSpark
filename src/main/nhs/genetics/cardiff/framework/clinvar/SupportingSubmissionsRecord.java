package nhs.genetics.cardiff.framework.clinvar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ml on 13/07/2017.
 */
public class SupportingSubmissionsRecord {

    @JsonProperty
    private String[] scv;

    @JsonProperty
    private String[] rcv;

    public String[] getScv() {
        return scv;
    }

    public String[] getRcv() {
        return rcv;
    }

}
