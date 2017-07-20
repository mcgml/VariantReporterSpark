package nhs.genetics.cardiff.framework;

import java.io.Serializable;

/**
 * A class for chromosomal changes.
 *
 * @author  Matt Lyon
 * @version 2.0
 * @since   2016-05-09
 */
public class GenomeVariant implements Serializable {

    private String contig, ref, alt;
    private int pos;

    public GenomeVariant(String contig, int pos, String ref, String alt){
        this.contig = contig;
        this.pos = pos;
        this.ref = ref;
        this.alt = alt;
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

                ++i; ++j; ++pos;
            }

            ref = ref.substring(i - 1);
            alt = alt.substring(i - 1);

        }

    }

    public String getContig() {
        return contig;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return contig + ":" + pos + ref + ">" + alt;
    }

}