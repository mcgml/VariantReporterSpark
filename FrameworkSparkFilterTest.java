package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ml on 16/06/2017.
 */
public class FrameworkSparkFilterTest {
    private final static GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

    @Test
    public void areAnyAlternativeAlleleCountsLow() throws Exception {

        ArrayList<Integer> counts = new ArrayList<>();
        counts.add(3,2);

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false), Allele.create("C", false)));
        variantContextBuilder.attribute("AC",counts);

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder
                        .name("sample2")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder
                        .name("sample3")
                        .alleles(Arrays.asList(Allele.create("C", false), Allele.create("C", false)))
                        .unfiltered()
                        .make()

        );

        System.out.println(variantContextBuilder);

        assertEquals(true, FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow(variantContextBuilder.make(), "sample1", 3));
        assertEquals(true, FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow(variantContextBuilder.make(), "sample2", 3));
        assertEquals(true, FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow(variantContextBuilder.make(), "sample3", 2));

        assertEquals(false, FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow(variantContextBuilder.make(), "sample1", 4));
        assertEquals(false, FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow(variantContextBuilder.make(), "sample2", 4));
        assertEquals(false, FrameworkSparkFilter.areAnyAlternativeAlleleCountsLow(variantContextBuilder.make(), "sample3", 3));
    }

    @Test
    public void areAnyAlternativeAllelesHighGnomadExomeFrequency() throws Exception {

    }

    @Test
    public void areAnyAlternativeAllelesHighGnomadGenomeFrequency() throws Exception {

    }
}