package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow;
import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.areAnyAlternativeAllelesHighGnomadExomeFrequency;
import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.areAnyAlternativeAllelesHighGnomadGenomeFrequency;


public class FemaleXDominantSparkFilter implements Function<VariantContext, Boolean> {
    private final String sample;

    public FemaleXDominantSparkFilter(String sample){
        this.sample = sample;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return FrameworkSparkFilter.x.contains(variantContext.getContig()) &&
                areAnyAlternativeAlleleCountsLow(variantContext, sample, 4) &&
                !areAnyAlternativeAllelesHighGnomadExomeFrequency(variantContext, sample, 0.001) &&
                !areAnyAlternativeAllelesHighGnomadGenomeFrequency(variantContext, sample, 0.01);
    }

}
