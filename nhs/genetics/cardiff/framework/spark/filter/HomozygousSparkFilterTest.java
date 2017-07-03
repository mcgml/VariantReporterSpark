package nhs.genetics.cardiff.framework.spark.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.gatk.engine.samples.Gender;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ml on 28/06/2017.
 */
public class HomozygousSparkFilterTest {
    @Test
    public void passAutosomal() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{2});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", new double[]{0.00001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.00001});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new HomozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
    @Test
    public void passXFemale() throws Exception {

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

        assertEquals(true, new HomozygousSparkFilter("sample", Gender.FEMALE).call(variantContextBuilder.make()));
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

        assertEquals(false, new HomozygousSparkFilter("sample", Gender.MALE).call(variantContextBuilder.make()));
    }
    @Test
    public void failHet() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{2});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new HomozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
    @Test
    public void failHomRef() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{0});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new HomozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
    @Test
    public void failAutosomalHighAc() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{6});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new HomozygousSparkFilter("sample", Gender.UNKNOWN).call(variantContextBuilder.make()));
    }
    @Test
    public void failXHighAc() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{8});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new HomozygousSparkFilter("sample", Gender.FEMALE).call(variantContextBuilder.make()));
    }
    @Test
    public void failXHighGenomeAf() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{2});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", new double[]{0.1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.00001});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new HomozygousSparkFilter("sample", Gender.FEMALE).call(variantContextBuilder.make()));
    }
    @Test
    public void failXHighExomeAf() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.attribute("AC", new int[]{2});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chrX.AF_POPMAX", new double[]{0.000001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.1});
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new HomozygousSparkFilter("sample", Gender.FEMALE).call(variantContextBuilder.make()));
    }
}