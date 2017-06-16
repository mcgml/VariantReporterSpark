package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.apache.spark.api.java.function.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.retainedFunctionalConsequences;

public class FunctionalConsequenceSparkFilter implements Function<VariantContext, Boolean> {
    private final String sample;
    private final String[] vepHeaders;

    public FunctionalConsequenceSparkFilter(final String sample, final String[] vepHeaders){
        this.sample = sample;
        this.vepHeaders = vepHeaders;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        HashSet<VepAnnotationObject> vepAnnotationObjects = new HashSet<>();

        if (variantContext.hasAttribute("CSQ")){

            //map vep
            try {
                vepAnnotationObjects.add(VepAnnotationObject.deserialiseVepAnnotation(vepHeaders, (String) variantContext.getAttribute("CSQ")));
            } catch (ClassCastException e) {
                for (String field : (ArrayList<String>) variantContext.getAttribute("CSQ")) {
                    vepAnnotationObjects.add(VepAnnotationObject.deserialiseVepAnnotation(vepHeaders, field));
                }
            }

            //check all alleles for pathogenicity
            for (Allele allele : variantContext.getGenotype(sample).getAlleles()){
                if (allele.isNonReference()){

                    int alleleNum = variantContext.getAlleleIndex(allele);

                    //check variant consequences for pathogenicity
                    for (VepAnnotationObject vepAnnotationObject : vepAnnotationObjects){
                        if (vepAnnotationObject.getAlleleNum() == alleleNum){

                            for (String consequence : vepAnnotationObject.getConsequence()) {
                                if (retainedFunctionalConsequences.contains(consequence)){
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
