package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.gatk.engine.samples.Gender;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ml on 29/06/2017.
 */
public class CompoundHeterozygousSparkFilterTest {
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

        assertEquals(true, new CompoundHeterozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
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

        assertEquals(true, new CompoundHeterozygousSparkFilter("sample", Gender.FEMALE).call(variantContextBuilder.make()));
    }
    @Test
    public void failAutosomalHom() throws Exception {
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

        assertEquals(false, new CompoundHeterozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
    @Test
    public void failXMale() throws Exception {
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

        assertEquals(false, new CompoundHeterozygousSparkFilter("sample", Gender.MALE).call(variantContextBuilder.make()));
    }
    @Test
    public void failHighGenomeAf() throws Exception {
        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.00001});
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new CompoundHeterozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
    @Test
    public void failHighExomeAf() throws Exception {
        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.000001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.1});
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new CompoundHeterozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
}