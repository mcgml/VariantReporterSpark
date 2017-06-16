package nhs.genetics.cardiff.framework;

import htsjdk.variant.variantcontext.GenotypeType;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;

import java.io.Serializable;
import java.util.List;

/**
 * A class for chromosomal changes.
 *
 * @author  Matt Lyon
 * @version 2.0
 * @since   2016-05-09
 */
public class GenomeVariant implements Serializable {

    private String contig, ref, alt;
    private int start, end;
    private GenotypeType genotypeType;
    private List<VepAnnotationObject> annotations;
    private Integer count;
    private Double frequency;

    public GenomeVariant(String contig, int start, int end, String ref, String alt, GenotypeType genotypeType){
        this.contig = contig;
        this.start = start;
        this.end = end;
        this.ref = ref;
        this.alt = alt;
        this.genotypeType = genotypeType;
    }

    public void convertToMinimalRepresentation(){

        if (ref.length() == 1 && alt.length() == 1){
            return;
        } else {
            int i = ref.length(), j = alt.length();

            //trim shared suffix sequence
            while (i > 1 && j > 1){

                if (ref.charAt(i - 1) != alt.charAt(j - 1)){
                    break;
                }

                --i; --j;
            }

            ref = ref.substring(0, i);
            alt = alt.substring(0, j);

            //trim shared prefix sequence
            i = 1; j = 1;

            while (i < ref.length() && j < alt.length()){

                if (ref.charAt(i - 1) != alt.charAt(j - 1)){
                    break;
                }

                ++i; ++j; ++start; --end;
            }

            ref = ref.substring(i - 1);
            alt = alt.substring(i - 1);

        }

    }

    public String getContig(){
        return contig;
    }
    public String getRef(){
        return ref;
    }
    public String getAlt(){
        return alt;
    }
    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public boolean isSnp(){
        return (ref.length() == 1 && alt.length() == 1);
    }
    public boolean isIndel(){
        return (ref.length() != 1 || alt.length() != 1);
    }
    public GenotypeType getGenotypeType() {
        return genotypeType;
    }
    public List<VepAnnotationObject> getAnnotations() {
        return annotations;
    }
    public Integer getCount() {
        return count;
    }
    public Double getFrequency() {
        return frequency;
    }

    public void setAnnotations(List<VepAnnotationObject> annotations) {
        this.annotations = annotations;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }
}