package nhs.genetics.cardiff.framework.panelapp;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by ml on 20/06/2017.
 */
public class Result {

    @JsonProperty("ModeOfInheritance")
    private ModeOfInheritance modeOfInheritance;

    @JsonProperty("EnsembleGeneIds")
    private String[] ensembleGeneIds;

    @JsonProperty("Evidences")
    private String[] evidences;

    @JsonProperty("Publications")
    private String[] publications;

    @JsonProperty("DiseaseGroup")
    private String diseaseGroup;

    @JsonProperty("LevelOfConfidence")
    private String levelOfConfidence;

    @JsonProperty("SpecificDiseaseName")
    private String specificDiseaseName;

    @JsonProperty("DiseaseSubGroup")
    private String diseaseSubGroup;

    @JsonProperty("ModeOfPathogenicity")
    private String[] modeOfPathogenicity;

    @JsonProperty("Phenotypes")
    private String[] phenotypes;

    @JsonProperty("Penetrance")
    private String penetrance;

    @JsonProperty("version")
    private Double version;

    @JsonProperty("GeneSymbol")
    private String geneSymbol;

    public ModeOfInheritance getModeOfInheritance() {
        return modeOfInheritance;
    }

    public String[] getEnsembleGeneIds() {
        return ensembleGeneIds;
    }

    public String[] getEvidences() {
        return evidences;
    }

    public String[] getPublications() {
        return publications;
    }

    public String getDiseaseGroup() {
        return diseaseGroup;
    }

    public String getLevelOfConfidence() {
        return levelOfConfidence;
    }

    public String getSpecificDiseaseName() {
        return specificDiseaseName;
    }

    public String getDiseaseSubGroup() {
        return diseaseSubGroup;
    }

    public String[] getModeOfPathogenicity() {
        return modeOfPathogenicity;
    }

    public String[] getPhenotypes() {
        return phenotypes;
    }

    public String getPenetrance() {
        return penetrance;
    }

    public Double getVersion() {
        return version;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }
}
