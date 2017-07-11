package nhs.genetics.cardiff.filters;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.apache.spark.api.java.function.Function;

import java.util.HashSet;

public class FunctionalConsequenceSparkFilter implements Function<VariantContext, Boolean> {
    private String sample;
    private String[] vepHeaders;

    public FunctionalConsequenceSparkFilter(String sample, String[] vepHeaders){
        this.sample = sample;
        this.vepHeaders = vepHeaders;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        if (variantContext.hasAttribute("CSQ")){
            HashSet<VepAnnotationObject> vepAnnotationObjects = VepAnnotationObject.getVepAnnotationObjects(vepHeaders, variantContext.getAttribute("CSQ"));

            //check all alleles for pathogenicity
            for (Allele allele : variantContext.getGenotype(sample).getAlleles()){
                if (allele.isNonReference()){

                    int alleleNum = FrameworkSparkFilter.getVepAlleleNumIndex(variantContext, allele);

                    //check variant consequences for pathogenicity
                    for (VepAnnotationObject vepAnnotationObject : vepAnnotationObjects){
                        if (vepAnnotationObject.getAlleleNum() == alleleNum){

                            for (String consequence : vepAnnotationObject.getConsequence()) {
                                if (FrameworkSparkFilter.functionalCodingImpact.contains(consequence)){
                                    return true;
                                }
                            }

                        }
                    }

                }
            }

        }

        return false;
    }

}
