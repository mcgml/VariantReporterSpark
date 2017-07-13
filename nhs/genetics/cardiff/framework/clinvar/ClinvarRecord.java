package nhs.genetics.cardiff.framework.clinvar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ml on 13/07/2017.
 */
@JsonIgnoreProperties({ "obj_type", "accession", "accession_version", "record_status", "gene_sort", "chr_sort", "location_sort", "variation_set_name", "variation_set_id", "genes", "variation_set", "trait_set" })
public class ClinvarRecord {

    @JsonProperty
    private long uid;

    @JsonProperty
    private String title;

    @JsonProperty("supporting_submissions")
    private SupportingSubmissionsRecord supportingSubmissionsRecord;

    @JsonProperty("clinical_significance")
    private ClinicalSignificanceRecord clinicalSignificanceRecord;

    @JsonProperty("record_status")
    private String recordStatus;

    public long getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public SupportingSubmissionsRecord getSupportingSubmissionsRecord() {
        return supportingSubmissionsRecord;
    }

    public ClinicalSignificanceRecord getClinicalSignificanceRecord() {
        return clinicalSignificanceRecord;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClinvarRecord that = (ClinvarRecord) o;

        return uid == that.uid;
    }

    @Override
    public int hashCode() {
        return (int) (uid ^ (uid >>> 32));
    }
}
