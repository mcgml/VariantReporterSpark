package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.areAnyAlternativeAlleleFrequencyLow;
import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.areAnyAlternativeAllelesHighGnomadExomeFrequency;
import static nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter.areAnyAlternativeAllelesHighGnomadGenomeFrequency;

//TODO parition by gene
public class AutosomalRecessiveSparkFilter implements Function<VariantContext, Boolean> {
    private final String sample;

    public AutosomalRecessiveSparkFilter(String sample){
        this.sample = sample;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return FrameworkSparkFilter.autosomes.contains(variantContext.getContig()) &&
                areAnyAlternativeAlleleFrequencyLow(variantContext, sample, 0.1) &&
                !areAnyAlternativeAllelesHighGnomadExomeFrequency(variantContext, sample, 0.05) &&
                !areAnyAlternativeAllelesHighGnomadGenomeFrequency(variantContext, sample, 0.05);

    }

}
