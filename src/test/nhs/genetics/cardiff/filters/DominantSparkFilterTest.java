package nhs.genetics.cardiff.filters;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.gatk.engine.samples.Gender;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test for Dominant workflow
 */
public class DominantSparkFilterTest {
    @Test
    public void passAutosomal() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new DominantSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
    @Test
    public void passXFemale() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new DominantSparkFilter("sample", Gender.FEMALE).call(variantContextBuilder.make()));
    }
    @Test
    public void passXMaleHemiDom() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{2});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new DominantSparkFilter("sample", Gender.MALE).call(variantContextBuilder.make()));
    }
    @Test
    public void passYMaleHemiDom() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "Y", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new DominantSparkFilter("sample", Gender.MALE).call(variantContextBuilder.make()));
    }
    @Test
    public void failHom() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{2});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new DominantSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
}