package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by ml on 16/06/2017.
 */
public class NonVariantBySampleSparkFilterTest {

    private final static ArrayList<Genotype> genotypes = new ArrayList<>();
    private final static GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

    @Test
    public void call() throws Exception {

        //poly
        genotypes.add(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        //ref
        genotypes.add(
                genotypeBuilder
                        .name("sample2")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make()
        );

        //no call
        genotypes.add(
                genotypeBuilder
                        .name("sample3")
                        .alleles(Arrays.asList(Allele.NO_CALL, Allele.NO_CALL))
                        .unfiltered()
                        .make()
        );

        //poly filtered
        genotypes.add(
                genotypeBuilder
                        .name("sample4")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .filter("LowDP")
                        .make()
        );

        //variant context
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "chr1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.unfiltered();
        variantContextBuilder.genotypes(genotypes);
        VariantContext variantContext = variantContextBuilder.make();

        //check results
        NonVariantBySampleSparkFilter nonVariantBySampleSparkFilter1 = new NonVariantBySampleSparkFilter("sample1");
        assertEquals(true, nonVariantBySampleSparkFilter1.call(variantContext));

        NonVariantBySampleSparkFilter nonVariantBySampleSparkFilter2 = new NonVariantBySampleSparkFilter("sample2");
        assertEquals(false, nonVariantBySampleSparkFilter2.call(variantContext));

        NonVariantBySampleSparkFilter nonVariantBySampleSparkFilter3 = new NonVariantBySampleSparkFilter("sample3");
        assertEquals(false, nonVariantBySampleSparkFilter3.call(variantContext));
        
        NonVariantBySampleSparkFilter nonVariantBySampleSparkFilter4 = new NonVariantBySampleSparkFilter("sample4");
        assertEquals(false, nonVariantBySampleSparkFilter4.call(variantContext));

    }

}

