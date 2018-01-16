package nhs.genetics.cardiff.filters;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.hellbender.utils.samples.Sex;

public class CompoundHeterozygousSparkFilter implements Function<VariantContext, Boolean> {
    private String sample;
    private Sex sex;

    /**
     * Identify candidate compound het variants
     * @param sample
     * @param sex
     */
    public CompoundHeterozygousSparkFilter(String sample, Sex sex){
        this.sample = sample;
        this.sex = sex;
    }

    @Override
    public Boolean call(VariantContext variantContext) {

        if (FrameworkSparkFilter.autosomes.contains(variantContext.getContig()) || (sex == Sex.FEMALE && FrameworkSparkFilter.x.contains(variantContext.getContig()))){
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
