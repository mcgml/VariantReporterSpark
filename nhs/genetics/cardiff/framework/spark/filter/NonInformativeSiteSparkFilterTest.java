package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test for NonInformativeSiteSparkFilter filter
 */
public class NonInformativeSiteSparkFilterTest {

    private String contig = "1";

    @Test
    public void pass() throws Exception {
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", contig, 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("CSQ", new Object());
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.unfiltered();

        assertEquals(true, new NonInformativeSiteSparkFilter().call(variantContextBuilder.make()));
    }

    @Test
    public void noCsq() throws Exception {
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", contig, 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.unfiltered();

        assertEquals(false, new NonInformativeSiteSparkFilter().call(variantContextBuilder.make()));
    }

    @Test
    public void zeroAc() throws Exception {
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", contig, 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("CSQ", new Object());
        variantContextBuilder.attribute("AC", new int[]{0});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.unfiltered();

        assertEquals(false, new NonInformativeSiteSparkFilter().call(variantContextBuilder.make()));
    }

    @Test
    public void highGenomeAf() throws Exception {
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", contig, 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("CSQ", new Object());
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.unfiltered();

        assertEquals(false, new NonInformativeSiteSparkFilter().call(variantContextBuilder.make()));
    }

    @Test
    public void highExomeAf() throws Exception {
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", contig, 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("CSQ", new Object());
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.1});
        variantContextBuilder.unfiltered();

        assertEquals(false, new NonInformativeSiteSparkFilter().call(variantContextBuilder.make()));
    }

    @Test
    public void filtered() throws Exception {
        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", contig, 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("CSQ", new Object());
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.filter("LowDP");

        assertEquals(false, new NonInformativeSiteSparkFilter().call(variantContextBuilder.make()));
    }

}