package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.gatk.engine.samples.Gender;

public class DominantSparkFilter implements Function<VariantContext, Boolean> {
    private String sample;
    private Gender gender;

    /**
     * Identifies rare heterozygous variants
     * @param sample
     * @param gender
     */
    public DominantSparkFilter(String sample, Gender gender){
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
                            .filter(allele -> FrameworkSparkFilter.getCohortAlternativeAlleleCount(variantContext, allele) < 4)
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.001)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.075)
                            .count() > 0;
        } else if (FrameworkSparkFilter.x.contains(variantContext.getContig()) && gender == Gender.MALE){
            return variantContext.getGenotype(sample).isHom() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> FrameworkSparkFilter.getCohortAlternativeAlleleCount(variantContext, allele) < 5)
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.001)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.075)
                            .count() > 0;
        }

        return false;
    }

}

