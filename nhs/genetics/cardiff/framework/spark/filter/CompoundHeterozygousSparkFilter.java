package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.gatk.engine.samples.Gender;

public class CompoundHeterozygousSparkFilter implements Function<VariantContext, Boolean> {
    private String sample;
    private Gender gender;

    /**
     * Identify candidate compound het variants
     * @param sample
     * @param gender
     */
    public CompoundHeterozygousSparkFilter(String sample, Gender gender){
        this.sample = sample;
        this.gender = gender;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return FrameworkSparkFilter.autosomes.contains(variantContext.getContig()) ||
                (FrameworkSparkFilter.x.contains(variantContext.getContig()) && gender == Gender.FEMALE) &&
                        variantContext.getGenotype(sample).isHet() &&
                        variantContext.getGenotype(sample).getAlleles()
                                .stream()
                                .filter(Allele::isNonReference)
                                .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                                .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                                .count() > 0;
    }

}
