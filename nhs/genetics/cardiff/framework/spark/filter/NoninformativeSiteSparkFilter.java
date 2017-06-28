package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

public class NoninformativeSiteSparkFilter implements Function<VariantContext, Boolean> {

    @Override
    public Boolean call(VariantContext variantContext) {
        return variantContext.isNotFiltered() &&
                (FrameworkSparkFilter.x.contains(variantContext.getContig()) || FrameworkSparkFilter.autosomes.contains(variantContext.getContig())) &&
                variantContext.hasAttribute("CSQ") &&
                FrameworkSparkFilter.getMaxAlleleCount(variantContext) > 0 &&
                FrameworkSparkFilter.getMinGnomadExomeAlleleFrequency(variantContext) <= 0.01 &&
                FrameworkSparkFilter.getMinGnomadGenomeAlleleFrequency(variantContext) <= 0.01;
    }

}

