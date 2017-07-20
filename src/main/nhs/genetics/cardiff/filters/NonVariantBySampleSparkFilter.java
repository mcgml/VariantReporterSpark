package nhs.genetics.cardiff.filters;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

public class NonVariantBySampleSparkFilter implements Function<VariantContext, Boolean> {
    private final String sample;

    public NonVariantBySampleSparkFilter(String sample){
        this.sample = sample;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return !variantContext.getGenotype(sample).isFiltered() &&
                !variantContext.getGenotype(sample).isHomRef() &&
                !variantContext.getGenotype(sample).isNoCall();
    }
}
