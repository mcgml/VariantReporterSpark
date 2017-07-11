package nhs.genetics.cardiff.filters;

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

        if (FrameworkSparkFilter.autosomes.contains(variantContext.getContig()) || (gender == Gender.FEMALE && FrameworkSparkFilter.x.contains(variantContext.getContig()))){
            return variantContext.getGenotype(sample).isHet() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .count() > 0;
        }

        return false;
    }


}
