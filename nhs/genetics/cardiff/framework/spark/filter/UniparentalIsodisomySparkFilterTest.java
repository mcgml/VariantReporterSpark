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
public class UniparentalIsodisomySparkFilterTest {
    @Test
    public void passAutosomalPaternal() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("father")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("mother")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new UniparentalIsodisomySparkFilter("sample", Gender.UNKNOWN, "father", "mother").call(variantContextBuilder.make()));
    }
    @Test
    public void passAutosomalMaternal() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("father")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("mother")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new UniparentalIsodisomySparkFilter("sample", Gender.UNKNOWN, "father", "mother").call(variantContextBuilder.make()));
    }
    @Test
    public void failAutosomal() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("father")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("mother")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new UniparentalIsodisomySparkFilter("sample", Gender.UNKNOWN, "father", "mother").call(variantContextBuilder.make()));
    }
    @Test
    public void passXFemaleMaternal() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("father")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("mother")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new UniparentalIsodisomySparkFilter("sample", Gender.FEMALE, "father", "mother").call(variantContextBuilder.make()));
    }
    @Test
    public void passXFemalePaternal() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("father")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("mother")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make()
        );

        assertEquals(true, new UniparentalIsodisomySparkFilter("sample", Gender.FEMALE, "father", "mother").call(variantContextBuilder.make()));
    }
    @Test
    public void failXMale() throws Exception {

        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "X", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false)));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("T", false), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("father")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("mother")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(false, new UniparentalIsodisomySparkFilter("sample", Gender.MALE, "father", "mother").call(variantContextBuilder.make()));
    }
}