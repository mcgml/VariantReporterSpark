package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ml on 16/06/2017.
 */
public class MaleXDominantSparkFilterTest {

    private final static GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

    @Test
    public void call() throws Exception {

        //variant 1 -- include
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC",2);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", 0.0005);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", 0.001);


        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        MaleXDominantSparkFilter maleXDominantSparkFilter = new MaleXDominantSparkFilter("sample1");
        assertEquals(true, maleXDominantSparkFilter.call(variantContextBuilder.make()));

        //variant 2 -- remove (not X)
        variantContextBuilder = new VariantContextBuilder("test", ".", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC",2);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", 0.0005);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", 0.001);

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        maleXDominantSparkFilter = new MaleXDominantSparkFilter("sample1");
        assertEquals(false, maleXDominantSparkFilter.call(variantContextBuilder.make()));

        //variant 3 -- remove (not HOM)
        variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC",1);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", 0.0005);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", 0.001);

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        maleXDominantSparkFilter = new MaleXDominantSparkFilter("sample1");
        assertEquals(false, maleXDominantSparkFilter.call(variantContextBuilder.make()));

        //variant 4 -- remove (high AC)
        variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC",4);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", 0.0005);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", 0.001);

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        maleXDominantSparkFilter = new MaleXDominantSparkFilter("sample1");
        assertEquals(false, maleXDominantSparkFilter.call(variantContextBuilder.make()));

        //variant 5 -- remove (high gnomad exome)
        variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC",3);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", 0.05);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", 0.001);

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        maleXDominantSparkFilter = new MaleXDominantSparkFilter("sample1");
        assertEquals(false, maleXDominantSparkFilter.call(variantContextBuilder.make()));

        //variant 6 -- remove (high gnomad genome)
        variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC",3);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", 0.0005);
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", 0.05);

        variantContextBuilder.genotypes(
                genotypeBuilder
                        .name("sample1")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        maleXDominantSparkFilter = new MaleXDominantSparkFilter("sample1");
        assertEquals(false, maleXDominantSparkFilter.call(variantContextBuilder.make()));

    }

}