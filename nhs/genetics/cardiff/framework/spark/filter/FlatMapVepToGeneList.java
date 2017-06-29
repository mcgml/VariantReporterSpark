package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ml on 22/06/2017.
 */
public class FlatMapVepToGeneList implements FlatMapFunction<VariantContext, String> {
    private String[] vepHeaders;

    public FlatMapVepToGeneList(String[] vepHeaders){
        this.vepHeaders = vepHeaders;
    }

    @Override
    public Iterator<String> call(VariantContext variantContext) throws Exception {
        HashSet<String> genes = new HashSet<>();

        if (variantContext.hasAttribute("CSQ")){
            HashSet<VepAnnotationObject> vepAnnotationObjects = VepAnnotationObject.getVepAnnotationObjects(vepHeaders, variantContext.getAttribute("CSQ"));
            for (VepAnnotationObject vepAnnotationObject : vepAnnotationObjects){
                if (vepAnnotationObject.getSymbol() != null){
                    genes.add(vepAnnotationObject.getSymbol());
                }
            }
        }

        return genes.iterator();
    }
}
