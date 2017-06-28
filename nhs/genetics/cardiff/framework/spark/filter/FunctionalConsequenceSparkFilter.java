package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.apache.spark.api.java.function.Function;

import java.util.ArrayList;
import java.util.HashSet;

import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.highFunctionalConsequence;
import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.moderateFunctionalConsequences;

public class FunctionalConsequenceSparkFilter implements Function<VariantContext, Boolean> {
    private final String sample;
    private final String[] vepHeaders;

    public FunctionalConsequenceSparkFilter(final String sample, final String[] vepHeaders){
        this.sample = sample;
        this.vepHeaders = vepHeaders;
    }

    @Override
    public Boolean call(VariantContext variantContext) {

        if (variantContext.hasAttribute("CSQ")){

            //map Vep
            HashSet<VepAnnotationObject> vepAnnotationObjects = VepAnnotationObject.getVepAnnotationObjects(vepHeaders, variantContext.getAttribute("CSQ"));

            //check all alleles for pathogenicity
            for (Allele allele : variantContext.getGenotype(sample).getAlleles()){
                if (allele.isNonReference()){

                    int alleleNum = FrameworkSparkFilter.getVepAlleleNumIndex(variantContext, allele);

                    //check variant consequences for pathogenicity
                    for (VepAnnotationObject vepAnnotationObject : vepAnnotationObjects){
                        if (vepAnnotationObject.getAlleleNum() == alleleNum){

                            for (String consequence : vepAnnotationObject.getConsequence()) {
                                if (highFunctionalConsequence.contains(consequence) || moderateFunctionalConsequences.contains(consequence)){
                                    return true;
                                }
                            }

                        }
                    }

                }
            }
        } else {
            return true;
        }

        return false;
    }
}
