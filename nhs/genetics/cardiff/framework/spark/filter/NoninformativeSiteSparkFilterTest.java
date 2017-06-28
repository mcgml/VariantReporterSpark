package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by ml on 16/06/2017.
 */
public class NoninformativeSiteSparkFilterTest {

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
        NoninformativeSiteSparkFilter nonVariantBySampleSparkFilter1 = new NoninformativeSiteSparkFilter();
        assertEquals(true, nonVariantBySampleSparkFilter1.call(variantContext));

        NoninformativeSiteSparkFilter nonVariantBySampleSparkFilter2 = new NoninformativeSiteSparkFilter();
        assertEquals(false, nonVariantBySampleSparkFilter2.call(variantContext));

        NoninformativeSiteSparkFilter nonVariantBySampleSparkFilter3 = new NoninformativeSiteSparkFilter();
        assertEquals(false, nonVariantBySampleSparkFilter3.call(variantContext));

        NoninformativeSiteSparkFilter nonVariantBySampleSparkFilter4 = new NoninformativeSiteSparkFilter();
        assertEquals(false, nonVariantBySampleSparkFilter4.call(variantContext));

    }

}

