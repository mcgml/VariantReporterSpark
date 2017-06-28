package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;

/**
 * Filter for insignificant VCF sites
 */
public class NonInformativeSiteSparkFilter implements Function<VariantContext, Boolean> {

    /**
     * Filter for insignificant VCF sites
     * @param variantContext
     * @return true is record is kept
     */
    @Override
    public Boolean call(VariantContext variantContext) {
        return variantContext.isNotFiltered() &&
                variantContext.hasAttribute("CSQ") &&
                FrameworkSparkFilter.getMaxAlleleCount(variantContext) > 0 &&
                FrameworkSparkFilter.areAnyAlternativeAllelesLowFrequency(variantContext, 0.01);

    }

}

