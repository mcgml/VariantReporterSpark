package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

public class NonInformativeSiteSparkFilter implements Function<VariantContext, Boolean> {

    @Override
    public Boolean call(VariantContext variantContext) {
        return variantContext.isNotFiltered() &&
                variantContext.hasAttribute("CSQ") &&
                FrameworkSparkFilter.getMaxAlleleCount(variantContext) > 0 &&
                FrameworkSparkFilter.getMinGnomadExomeAlleleFrequency(variantContext) <= 0.01 &&
                FrameworkSparkFilter.getMinGnomadGenomeAlleleFrequency(variantContext) <= 0.01;
    }

}

