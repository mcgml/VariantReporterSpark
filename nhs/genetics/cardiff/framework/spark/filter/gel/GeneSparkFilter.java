package nhs.genetics.cardiff.framework.spark.filter.gel;

import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.apache.spark.api.java.function.Function;

import java.util.HashSet;

public class GeneSparkFilter implements Function<VariantContext, Boolean> {
    private HashSet<String> symbols;
    private String[] vepHeaders;

    public GeneSparkFilter(HashSet<String> symbols, String[] vepHeaders){
        this.symbols = symbols;
        this.vepHeaders = vepHeaders;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        if (variantContext.hasAttribute("CSQ")){
            for (VepAnnotationObject vepAnnotationObject :VepAnnotationObject.getVepAnnotationObjects(vepHeaders, variantContext.getAttribute("CSQ"))){
                if (symbols.contains(vepAnnotationObject.getSymbol())){
                    return true;
                }
            }
        }

        return false;
    }

}

