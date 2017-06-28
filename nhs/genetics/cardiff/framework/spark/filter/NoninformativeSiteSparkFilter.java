package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.spark.filter.gel.GelFilterFramework;
import org.apache.spark.api.java.function.Function;

public class NoninformativeSiteSparkFilter implements Function<VariantContext, Boolean> {

    @Override
    public Boolean call(VariantContext variantContext) {
        return variantContext.isNotFiltered() &&
                (GelFilterFramework.x.contains(variantContext.getContig()) || GelFilterFramework.autosomes.contains(variantContext.getContig())) &&
                variantContext.hasAttribute("CSQ") &&
                GelFilterFramework.getMaxAlleleCount(variantContext) > 0 &&
                GelFilterFramework.getMinGnomadExomeAlleleFrequency(variantContext) <= 0.01 &&
                GelFilterFramework.getMinGnomadGenomeAlleleFrequency(variantContext) <= 0.01;
    }

}

