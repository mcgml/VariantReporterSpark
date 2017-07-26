package nhs.genetics.cardiff.framework.hgmd;

/**
 * Class for storing the HGMD result
 */
public class HGMDProBatchSearchResult {

    private Integer number;
    private String searchTerm;
    private String geneSymbol;
    private String hgmdMutation;
    private String hgvs;
    private String hg38Coordinate;
    private HGMDProVariantClass hgmdVariantClass;
    private String dbSnpIdentifier;
    private String hgmdAccession;

    public HGMDProBatchSearchResult(Integer number, String searchTerm, String geneSymbol, String hgmdMutation, String hgvs, String hg38Coordinate, HGMDProVariantClass hgmdVariantClass, String dbSnpIdentifier, String hgmdAccession){
        this.number = number;
        this.searchTerm = searchTerm;
        this.geneSymbol = geneSymbol;
        this.hgmdMutation = hgmdMutation;
        this.hgvs = hgvs;
        this.hg38Coordinate = hg38Coordinate;
        this.hgmdVariantClass = hgmdVariantClass;
        this.dbSnpIdentifier = dbSnpIdentifier;
        this.hgmdAccession = hgmdAccession;
    }

    public Integer getNumber() {
        return number;
    }
    public String getSearchTerm() {
        return searchTerm;
    }
    public String getGeneSymbol() {
        return geneSymbol;
    }
    public String getHgmdMutation() {
        return hgmdMutation;
    }
    public String getHgvs() {
        return hgvs;
    }
    public String getHg38Coordinate() {
        return hg38Coordinate;
    }
    public HGMDProVariantClass getHgmdVariantClass() {
        return hgmdVariantClass;
    }
    public String getDbSnpIdentifier() {
        return dbSnpIdentifier;
    }
    public String getHgmdAccession() {
        return hgmdAccession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HGMDProBatchSearchResult that = (HGMDProBatchSearchResult) o;

        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        if (searchTerm != null ? !searchTerm.equals(that.searchTerm) : that.searchTerm != null) return false;
        if (geneSymbol != null ? !geneSymbol.equals(that.geneSymbol) : that.geneSymbol != null) return false;
        if (hgmdMutation != null ? !hgmdMutation.equals(that.hgmdMutation) : that.hgmdMutation != null) return false;
        if (hgvs != null ? !hgvs.equals(that.hgvs) : that.hgvs != null) return false;
        if (hg38Coordinate != null ? !hg38Coordinate.equals(that.hg38Coordinate) : that.hg38Coordinate != null)
            return false;
        if (hgmdVariantClass != that.hgmdVariantClass) return false;
        if (dbSnpIdentifier != null ? !dbSnpIdentifier.equals(that.dbSnpIdentifier) : that.dbSnpIdentifier != null)
            return false;
        return hgmdAccession != null ? hgmdAccession.equals(that.hgmdAccession) : that.hgmdAccession == null;
    }

    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        result = 31 * result + (searchTerm != null ? searchTerm.hashCode() : 0);
        result = 31 * result + (geneSymbol != null ? geneSymbol.hashCode() : 0);
        result = 31 * result + (hgmdMutation != null ? hgmdMutation.hashCode() : 0);
        result = 31 * result + (hgvs != null ? hgvs.hashCode() : 0);
        result = 31 * result + (hg38Coordinate != null ? hg38Coordinate.hashCode() : 0);
        result = 31 * result + (hgmdVariantClass != null ? hgmdVariantClass.hashCode() : 0);
        result = 31 * result + (dbSnpIdentifier != null ? dbSnpIdentifier.hashCode() : 0);
        result = 31 * result + (hgmdAccession != null ? hgmdAccession.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        return number + "\t" + searchTerm + "\t" + geneSymbol + "\t" + hgmdMutation  + "\t" + hgvs +'\t' + hg38Coordinate + "\t" + hgmdVariantClass + "\t" + dbSnpIdentifier + "\t" + hgmdAccession;
    }
}
