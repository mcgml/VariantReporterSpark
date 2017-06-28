package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.gatk.engine.samples.Gender;

public class DeNovoSparkFilter implements Function<VariantContext, Boolean> {
    private String sample, father, mother;
    private Gender gender;

    public DeNovoSparkFilter(String sample, Gender gender, String father, String mother){
        this.sample = sample;
        this.gender = gender;
        this.father = father;
        this.mother = mother;
    }

    @Override
    public Boolean call(VariantContext variantContext) {

        if (GelFilterFramework.autosomes.contains(variantContext.getContig())){
            return variantContext.getGenotype(sample).isHet() &&
                    variantContext.getGenotype(father).isHomRef() &&
                    variantContext.getGenotype(mother).isHomRef() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> GelFilterFramework.getCohortAlternativeAlleleCount(variantContext, allele) < 4)
                            .filter(allele -> GelFilterFramework.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.001)
                            .filter(allele -> GelFilterFramework.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.075)
                            .count() > 0;
        } else if (GelFilterFramework.x.contains(variantContext.getContig())){
            return (gender == Gender.MALE && variantContext.getGenotype(sample).isHomVar()) || (gender == Gender.FEMALE && variantContext.getGenotype(sample).isHet()) &&
                    variantContext.getGenotype(father).isHomRef() &&
                    variantContext.getGenotype(mother).isHomRef() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> GelFilterFramework.getCohortAlternativeAlleleCount(variantContext, allele) < 4)
                            .filter(allele -> GelFilterFramework.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.001)
                            .filter(allele -> GelFilterFramework.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.075)
                            .count() > 0;
        }

        return false;
    }

}
