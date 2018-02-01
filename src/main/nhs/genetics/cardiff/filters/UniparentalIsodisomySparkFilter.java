package nhs.genetics.cardiff.filters;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.broadinstitute.hellbender.utils.samples.Sex;

public class UniparentalIsodisomySparkFilter implements Function<VariantContext, Boolean> {
    private String sample, father, mother;
    private Sex sex;

    public UniparentalIsodisomySparkFilter(String sample, Sex sex, String father, String mother){
        this.sample = sample;
        this.sex = sex;
        this.father = father;
        this.mother = mother;
    }

    @Override
    public Boolean call(VariantContext variantContext) {

        if (FrameworkSparkFilter.autosomes.contains(variantContext.getContig())){
            return variantContext.getGenotype(sample).isHomVar() &&
                    ((variantContext.getGenotype(mother).isHet() && variantContext.getGenotype(father).isHomRef()) || (variantContext.getGenotype(mother).isHomRef() && variantContext.getGenotype(father).isHet())) &&
                    !variantContext.getGenotype(mother).isFiltered() &&
                    !variantContext.getGenotype(father).isFiltered() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> !FrameworkSparkFilter.isAlleleSpanningDeletion(allele))
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .count() > 0;
        } else if (FrameworkSparkFilter.x.contains(variantContext.getContig()) && sex == Sex.FEMALE){
            return variantContext.getGenotype(sample).isHomVar() &&
                    ((variantContext.getGenotype(mother).isHomRef() && variantContext.getGenotype(father).isHomVar()) || (variantContext.getGenotype(mother).isHet() && variantContext.getGenotype(father).isHomRef())) &&
                    !variantContext.getGenotype(mother).isFiltered() &&
                    !variantContext.getGenotype(father).isFiltered() &&
                    variantContext.getGenotype(sample).getAlleles()
                            .stream()
                            .filter(Allele::isNonReference)
                            .filter(allele -> !FrameworkSparkFilter.isAlleleSpanningDeletion(allele))
                            .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.01)
                            .count() > 0;
        }

        return false;
    }

}
