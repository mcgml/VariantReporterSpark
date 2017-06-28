package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Test for NonVariantBySampleSparkFilter filter
 */
public class NonVariantBySampleSparkFilterTest {

    @Test
    public void pass() throws Exception{
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new NonVariantBySampleSparkFilter("sample").call(variantContextBuilder.make()));
    }

    @Test
    public void filtered() throws Exception{
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .filter("LowDP")
                        .make()
        );

        assertEquals(false, new NonVariantBySampleSparkFilter("sample").call(variantContextBuilder.make()));
    }

    @Test
    public void homRef() throws Exception{
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new NonVariantBySampleSparkFilter("sample").call(variantContextBuilder.make()));
    }

    @Test
    public void noCall() throws Exception{
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample")
                        .alleles(Arrays.asList(Allele.NO_CALL, Allele.NO_CALL))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new NonVariantBySampleSparkFilter("sample").call(variantContextBuilder.make()));
    }

}

