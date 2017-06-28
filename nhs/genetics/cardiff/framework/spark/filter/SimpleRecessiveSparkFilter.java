package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.gatk.engine.samples.Gender;

public class SimpleRecessiveSparkFilter implements Function<VariantContext, Boolean> {
    private String sample, father, mother;
    private Gender gender;

    /**
     * Identifies low frequency homozygous calls
     * @param sample
     * @param gender
     */
    public SimpleRecessiveSparkFilter(String sample, Gender gender){
        this.sample = sample;
        this.gender = gender;
    }

    /**
     * Identifies low frequency homozygous calls with one allele coming from each parent
     * @param sample
     * @param gender
     */
    public SimpleRecessiveSparkFilter(String sample, Gender gender, String father, String mother){
        this.sample = sample;
        this.gender = gender;
        this.father = father;
        this.mother = mother;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        if (GelFilterFramework.autosomes.contains(variantContext.getContig())) {
            return variantContext.getGenotype(sample).isHomVar() &&
                    (father != null && variantContext.getGenotype(father).isHet()) &&
                    (mother != null && variantContext.getGenotype(mother).isHet()) &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> GelFilterFramework.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .filter(allele -> GelFilterFramework.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .count() > 0;
        } else if (GelFilterFramework.x.contains(variantContext.getContig()) && gender == Gender.MALE){
            return variantContext.getGenotype(sample).isHomVar() &&
                    (father != null && variantContext.getGenotype(father).isHomRef()) &&
                    (mother != null && variantContext.getGenotype(mother).isHet()) &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> GelFilterFramework.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .filter(allele -> GelFilterFramework.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .count() > 0;
        } else if (GelFilterFramework.x.contains(variantContext.getContig()) && gender == Gender.FEMALE){
            return !variantContext.getGenotype(sample).isHomRef() &&
                    (father != null && variantContext.getGenotype(father).isHomRef()) &&
                    (mother != null && !variantContext.getGenotype(mother).isHomVar()) &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> GelFilterFramework.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .filter(allele -> GelFilterFramework.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .count() > 0;
        }
        return false;
    }

}
