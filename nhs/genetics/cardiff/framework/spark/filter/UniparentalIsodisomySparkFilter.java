package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.gatk.engine.samples.Gender;

public class UniparentalIsodisomySparkFilter implements Function<VariantContext, Boolean> {
    private String sample, father, mother;
    private Gender gender;

    public UniparentalIsodisomySparkFilter(String sample, Gender gender, String father, String mother){
        this.sample = sample;
        this.gender = gender;
        this.father = father;
        this.mother = mother;
    }

    @Override
    public Boolean call(VariantContext variantContext) {

        if (FrameworkSparkFilter.autosomes.contains(variantContext.getContig())){
            return variantContext.getGenotype(sample).isHomVar() &&
                    ((variantContext.getGenotype(mother).isHet() && variantContext.getGenotype(father).isHomRef()) || (variantContext.getGenotype(mother).isHomRef() && variantContext.getGenotype(father).isHet())) &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .count() > 0;
        } else if (FrameworkSparkFilter.x.contains(variantContext.getContig()) && gender == Gender.FEMALE){
            return variantContext.getGenotype(sample).isHomVar() &&
                    ((variantContext.getGenotype(mother).isHomRef() && variantContext.getGenotype(father).isHomVar()) || (variantContext.getGenotype(mother).isHet() && variantContext.getGenotype(father).isHomRef())) &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= 0.01)
                            .count() > 0;
        }

        return false;
    }

}
