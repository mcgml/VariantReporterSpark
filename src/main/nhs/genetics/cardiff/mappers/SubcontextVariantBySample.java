package nhs.genetics.cardiff.mappers;

import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.MapFunction;
import org.broadinstitute.gatk.engine.samples.Sample;

/**
 * Created by ml on 22/06/2017.
 */
public class SubcontextVariantBySample implements MapFunction<VariantContext, VariantContext> {
    private Sample sample;

    public SubcontextVariantBySample(Sample sample){
        this.sample = sample;
    }

    @Override
    public VariantContext call(VariantContext variantContext) throws Exception {
        return variantContext.subContextFromSample(sample.getID());
    }

}
