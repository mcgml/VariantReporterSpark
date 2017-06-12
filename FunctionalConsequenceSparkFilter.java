package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

public class FunctionalConsequenceSparkFilter implements Function<VariantContext, Boolean> {
    private final String sample;

    public FunctionalConsequenceSparkFilter(String sample){
        this.sample = sample;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return true;
    }
}
