package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.spark.FrameworkSparkFilter;
import org.apache.spark.api.java.function.Function;

import static nhs.genetics.cardiff.framework.spark.FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow;
import static nhs.genetics.cardiff.framework.spark.FrameworkSparkFilter.areAnyAlternativeAllelesHighGnomadExomeFrequency;
import static nhs.genetics.cardiff.framework.spark.FrameworkSparkFilter.areAnyAlternativeAllelesHighGnomadGenomeFrequency;

public class FemaleXDominantSparkFilter implements Function<VariantContext, Boolean> {
    private final String sample;

    public FemaleXDominantSparkFilter(String sample){
        this.sample = sample;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return FrameworkSparkFilter.x.contains(variantContext.getContig()) &&
                !variantContext.getGenotype(sample).isHomRef() &&
                areAnyAlternativeAlleleCountsLow(variantContext, sample, 4) &&
                !areAnyAlternativeAllelesHighGnomadExomeFrequency(variantContext, sample, 0.001) &&
                !areAnyAlternativeAllelesHighGnomadGenomeFrequency(variantContext, sample, 0.01);
    }

}
