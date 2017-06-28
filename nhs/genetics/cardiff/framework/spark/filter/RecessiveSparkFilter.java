package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.gatk.engine.samples.Gender;

public class RecessiveSparkFilter implements Function<VariantContext, Boolean> {
    private String sample;
    private Gender gender;

    /**
     * Identifies low frequency homozygous calls
     * @param sample
     * @param gender
     */
    public RecessiveSparkFilter(String sample, Gender gender){
        this.sample = sample;
        this.gender = gender;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        if (FrameworkSparkFilter.autosomes.contains(variantContext.getContig())){
            return variantContext.getGenotype(sample).isHomVar() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> FrameworkSparkFilter.getCohortAlternativeAlleleCount(variantContext, allele) < 6)
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .count() > 0;
        } else if (FrameworkSparkFilter.x.contains(variantContext.getContig()) && gender == Gender.FEMALE){
            return variantContext.getGenotype(sample).isHomVar() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> FrameworkSparkFilter.getCohortAlternativeAlleleCount(variantContext, allele) < 8)
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .count() > 0;
        }
        return false;
    }

}
