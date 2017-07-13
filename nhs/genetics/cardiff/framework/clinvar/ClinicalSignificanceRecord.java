package nhs.genetics.cardiff.framework.clinvar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ml on 13/07/2017.
 */
public class ClinicalSignificanceRecord {

    @JsonProperty("description")
    private String description;

    @JsonProperty("last_evaluated")
    private String lastEvaluated;

    @JsonProperty("review_status")
    private String reviewStatus;

    public String getDescription() {
        return description;
    }

    public String getLastEvaluated() {
        return lastEvaluated;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }
}
