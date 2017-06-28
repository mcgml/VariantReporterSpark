package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

public class DeNovoSparkFilter implements Function<VariantContext, Boolean> {
    private String proband, father, mother;

    public DeNovoSparkFilter(String proband, String father, String mother){
        this.proband = proband;
        this.father = father;
        this.mother = mother;
    }

    @Override
    public Boolean call(VariantContext variantContext) {
        return variantContext.getGenotype(father).isHomRef() &&
                variantContext.getGenotype(mother).isHomRef() &&
                variantContext.getGenotype(proband).getAlleles()
                        .stream()
                        .filter(Allele::isNonReference)
                        .filter(allele -> FrameworkSparkFilter.getCohortAlternativeAlleleCount(variantContext, allele) < 4)
                        .filter(allele -> FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) < 0.001)
                        .filter(allele -> FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) < 0.075)
                        .count() > 0;
    }

}
