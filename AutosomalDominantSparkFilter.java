package nhs.genetics.cardiff.framework.spark.filter.gel;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

public class AutosomalDominantSparkFilter implements Function<VariantContext, Boolean> {
    private String sample;

    /**
     * Identifies rare autosomal heterozgous variants
     * @param sample
     */
    public AutosomalDominantSparkFilter(String sample){
        this.sample = sample;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return GelFilterFramework.autosomes.contains(variantContext.getContig()) &&
                variantContext.getGenotype(sample).isHet() &&
                variantContext.getGenotype(sample).getAlleles()
                        .stream()
                        .filter(Allele::isNonReference)
                        .filter(allele -> GelFilterFramework.getCohortAlternativeAlleleCount(variantContext, allele) < 4)
                        .filter(allele -> GelFilterFramework.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.001)
                        .filter(allele -> GelFilterFramework.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.075)
                        .count() > 0;
    }

}

